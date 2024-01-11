# 익명 의견 교환 웹페이지
## 시작
해당 웹페이지는 http://localhost:8080/boards/ 에서 시작한다.

## 구현 방법

### Entity - Article
```java
@Data
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private String writer;
    private String password;

    @ManyToOne
    private Board board;

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @ElementCollection
    private Set<String> hashtags = new HashSet<>();

    @ElementCollection
    private List<String> images = new ArrayList<>();
}
```
### Entity - Board
```java
@Data
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    @OrderBy("id Desc")
    private List<Article> articles;
}

```

### Entity - Comment
```java
@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String writer;
    private String password;

    @ManyToOne
    private Article article;
}
```

### 필수 과제 - 게시판 목록
http://localhost:8080/boards/로 접속하면 BoardController의 boardList 메서드로 매핑이된다.
```java
// 게시판 목록
@GetMapping("/")
public String boardList(Model model) {
    model.addAttribute("boards", boardService.readBoardAll());
    return "board/board-list";
}
```
BoardService의 readBoardAll() 메서드는 JpaRepository인 boardRepository로 부터 findAll()을 이용해 모든 게시판 정보를 불러온다.
```java
// 모든 게시판을 가져오기
public List<Board> readBoardAll() {
    return boardRepository.findAll();
}
```

### 필수 과제 - 게시판 조회
http://localhost:8080/boards/1 과 같이 특정 게시판으로 접속하면 해당 게시판의 게시글들을 볼 수 있다.
```java
@GetMapping("/{boardId}")
public String boardView(
        @PathVariable("boardId") Long boardId,
        Model model
) {
    Board board = boardService.readBoard(boardId);
    model.addAttribute("board", board);
    return "board/board-articles";
}
```
boardService의 readBoard로부터 게시판의 id로 게시판을 반환받는다.
```java
public Board readBoard(Long id) {
    Optional<Board> optionalBoard = boardRepository.findById(id);
    return optionalBoard.orElse(null);
}
```
Board Entity에서 @OrderBy 어노테이션으로 정렬 방식을 지정해주었다.
```java
@OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
@OrderBy("id Desc")
private List<Article> articles;
```

### 필수 과제 - 전체 게시판 조회
http://localhost:8080/boards/entire  
전체 게시판의 경우에는 불러오는 게시글이 다른 게시판들과는 다르므로
엔드포인트를 별도로 두어 기능을 분리했다.
```java
// 전체 게시판 View
@GetMapping("/entire")
public String entireView(
        Model model
) {
    model.addAttribute("articles", articleService.readAllArticleDesc());
    return "board/entire-articles";
}
```
ArticleService의 readAllArticleDesc 메서드는 repository로부터 모든 게시글을 id를 기준으로 역순 정렬하여 가져온다.
```java
// 게시글을 역순으로 가져오기
public List<Article> readAllArticleDesc() {
    return articleRepository.findAllByOrderByIdDesc();
}
```

### 필수 과제 - 게시글 작성
http://localhost:8080/article/create  
Get방식으로 접근하면 게시글 작성 폼을 보여준다.
```java
// article 작성 폼
@GetMapping("/create")
public String createArticleView(
        Model model
) {
    model.addAttribute("boards", boardService.readBoardAll());
    return "article/article-create";
}
```
게시글 작성 폼으로부터 Post 요청을 받으면 Article을 생성해 articleSerivce의 saveArticle 메서드로 넘긴다.
```java
// article 생성 처리
@PostMapping("/create")
public String createArticle(
        @RequestParam("boardId") Long boardId,
        @RequestParam("writer") String writer,
        @RequestParam("password") String password,
        @RequestParam("title") String title,
        @RequestParam("content") String content
) {
    Board board = boardService.readBoard(boardId);

    Article article = new Article();
    article.setBoard(board);
    article.setWriter(writer);
    article.setPassword(password);
    article.setTitle(title);
    article.setContent(content);
    Long articleId = articleService.saveArticle(article);
    return "redirect:/article/" + articleId;
}
```
articleService에서 전달받은 article을 repository에 저장한다.
```java
// 게시글 저장
public Long saveArticle(Article article) {
    // 게시글 저장 전 hashtag 저장
    article.setHashtags(extractHashtags(article.getContent()));
    Article saved = articleRepository.save(article);
    return saved.getId();
}
```

### 필수 과제 - 게시글 읽기
http://localhost:8080/article/1  
@PathVariable을 이용해 게시글 id를 변수로 받고
articleService의 readArticle 메서드를 호출해서 해당 id의 게시글을 불러온다.

```java
// article 읽기 화면
@GetMapping("/{articleId}")
public String articleView(
        @PathVariable("articleId") Long id,
        Model model
) {
    model.addAttribute("article", articleService.readArticle(id));
    model.addAttribute("nextArticle", articleService.getNextArticle(id));
    model.addAttribute("prevArticle", articleService.getPrevArticle(id));
    return "article/article-view";
}
```
```java
// id로 게시글을 가져오기
public Article readArticle(Long id) {
    Optional<Article> optionalArticle = articleRepository.findById(id);
    return optionalArticle.orElse(null);
}
```

### 필수 과제 - 게시글 수정
http://localhost:8080/article/1/update  
Get 요청으로 접근 시 수정 폼으로 연결한다.  
@PathVariable로 articleId를 변수로 받아 해당 게시글을 불러오고 model로 전달해 수정 폼에서 해당 게시글의 데이터가 미리 입력되있도록 한다.
```java
// article 수정 폼
@GetMapping("/{articleId}/update")
public String updateArticleForm(
        @PathVariable("articleId") Long articleId,
        Model model
) {
    Article article = articleService.readArticle(articleId);
    model.addAttribute("article", article);
    return "article/article-update";
}
```
폼을 이용해서 Post요청으로 접근하게 되면 폼에서 입력한 사용자 비밀번호와 수정할 게시글 정보를 받아온다.  
해당 변수들을 articleService의 updateArticle 메서드로 넘겨서 수정 성공 여부를 판단한다.  
만약 수정 성공 시 원래 게시글로 돌아가고, 실패 시 실패 알림 화면으로 넘어간다.
```java
// article 수정
@PostMapping("/{articleId}/update")
public String updateArticle(
        @PathVariable("articleId") Long articleId,
        @RequestParam("password") String password,
        @RequestParam("writer") String writer,
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        Model model
) {
    // 수정 성공 시
    if (articleService.updateArticle(articleId, password, writer, title, content)) {
        return String.format("redirect:/article/%d", articleId);
    // 수정 실패 시
    } else {
        model.addAttribute("articleId", articleId);
        return "article/update-failed";
    }
}
```
articleService에서는 articleId로 불러온 게시글의 비밀번호와 입력받은 비밀번호가 같은지 판단하고 수정을 진행한 뒤 성공 여부를 반환한다.
```java
// 게시글 수정
public boolean updateArticle(
        Long id,
        String password,
        String writer,
        String title,
        String content
) {
    Article article = readArticle(id);
    if (article.getPassword().equals(password)) {
        article.setWriter(writer);
        article.setTitle(title);
        article.setContent(content);
        // 수정 시 해시태그 재추출 및 저장
        article.setHashtags(extractHashtags(content));
        articleRepository.save(article);
        return true;
    }
    return false;
}
```

### 필수 과제 - 게시글 삭제
http://localhost:8080/article/1/delete  
Get 요청으로 접근 시 삭제 폼을 반환한다.
```java
// article 삭제 폼
@GetMapping("/{articleId}/delete")
public String deleteArticleForm(
        @PathVariable("articleId") Long articleId,
        Model model
) {
    model.addAttribute("articleId", articleId);
    return "/article/article-delete";
}
```
삭제 폼으로부터 Post 요청을 받으면 articleService의 deleteArticle에 articleId와 password를 넘겨 삭제 성공 여부를 확인하고 성공 시 게시판 목록으로,
실패 시 실패 알림 화면으로 넘어간다.
```java
// article 삭제 처리
@PostMapping("/{articleId}/delete")
public String deleteArticle(
        @PathVariable("articleId") Long articleId,
        @RequestParam("password") String password,
        Model model
) throws IOException {
    // 삭제 성공시
    if (articleService.deleteArticle(articleId, password)) {
        return "redirect:/boards/";
    // 실패시
    } else {
        model.addAttribute("articleId", articleId);
        return "article/delete-failed";
    }
}
```
articleService의 deleteArticle 메서드에서 입력받은 articleId로 article을 불러오고 해당 article의 비밀번호가 입력받은 비밀번호와 같은지 판단하고 삭제를 진행한 후 삭제 성공 여부를 반환한다.
```java
// 게시글 삭제
public boolean deleteArticle(Long id, String password) throws IOException {
    Article article = readArticle(id);
    if (password.equals(article.getPassword())) {
        deleteAllImageFromArticle(id);
        articleRepository.deleteById(id);
        return true;
    }
    return false;
}
```

### 필수 과제 - 댓글 추가
게시글 화면에 포함되어있는 댓글 추가 Form을 이용해 Post 방식으로 요청받는다.  
Comment를 새롭게 생성하고 입력받은 댓글 정보들을 setter로 설정 후 commentService의 saveComment 메서드로 전달한다.
```java
// 댓글 추가 요청 처리
@PostMapping("/{articleId}/comment/")
public String createComment(
        @PathVariable("articleId") Long articleId,
        @RequestParam("comment_writer") String writer,
        @RequestParam("comment_password") String password,
        @RequestParam("comment_content") String content
) {
    Article article = articleService.readArticle(articleId);
    Comment comment = new Comment();
    comment.setArticle(article);
    comment.setWriter(writer);
    comment.setPassword(password);
    comment.setContent(content);
    commentService.saveComment(comment);
    return String.format("redirect:/article/%d", articleId);
}
```
aritcleService에서는 전달받은 댓글을 repository로 저장한다.
```java
// 댓글 저장
public void saveComment(Comment comment) {
    commentRepository.save(comment);
}
```

### 필수 과제 - 댓글 삭제
@Get 방식으로 요청받으면 댓글 삭제를 위한 비밀번호를 입력하는 폼으로 이동한다.
```java
// 댓글 삭제 요청 폼
@GetMapping("/{articleId}/comment/{commentId}/delete")
public String deleteCommentView(
        @PathVariable("articleId") Long articleId,
        @PathVariable("commentId") Long commentId,
        Model model
) {
    model.addAttribute("articleId", articleId);
    model.addAttribute("commentId", commentId);
    return "comment/comment-delete";
}
```
폼으로부터 Post방식으로 요청받으면 commentSerivce에 deleteComment 메서드를 통해 commentId와 password를 전달해 삭제를 진행한 뒤 삭제 성공 여부를 판단해 삭제 성공 시 기존 게시글로 이동, 삭제 실패 시 삭제 실패 안내페이지로 이동한다.
```java
// 댓글 삭제 요청 처리
@PostMapping("/{articleId}/comment/{commentId}/delete")
public String deleteComment(
        @PathVariable("articleId") Long articleId,
        @PathVariable("commentId") Long commentId,
        @RequestParam("password") String password,
        Model model
) {
    // 삭제 성공 시
    if (commentService.deleteComment(commentId, password)) {
        return "redirect:/article/" + articleId;
    // 삭제 실패 시
    } else {
        model.addAttribute("articleId", articleId);
        return "comment/delete-failed";
    }
}
```
컨트롤러로부터 입력받은 commentId와 password를 가지고 실제 댓글을 불러와 댓글의 패스워드와 입력받은 패스워드가 동일한지를 판단 후 삭제를 진행하고 삭제 성공 여부를 반환한다.
```java
// 댓글 삭제
public boolean deleteComment(Long commentId, String password) {
    Comment comment = commentRepository.findById(commentId).orElse(null);
    if (comment == null || !comment.getPassword().equals(password)) return false;
    commentRepository.deleteById(commentId);
    return true;
}
```

### 도전과제 - 해시태그 추출
게시글을 등록하거나 수정할 때, 게시글의 Content를 가져와 해시태그 추출해
Article Entity에 @ElementalCollection으로 어노테이션된 hashtags 필드에 추가해준다.
```java
// 게시글 저장
public Long saveArticle(Article article) {
    // 게시글 저장 전 hashtag 저장
    article.setHashtags(extractHashtags(article.getContent()));
    Article saved = articleRepository.save(article);
    return saved.getId();
}
```
```java
// 해시태그 추출 로직
private Set<String> extractHashtags(String content) {
    Set<String> hashtags = new HashSet<>();
    Pattern pattern = Pattern.compile("#([0-9a-zA-Z가-힣]+)"); // #으로 시작하는 단어 패턴

    Matcher matcher = pattern.matcher(content);
    while (matcher.find()) {
        String hashtag = matcher.group(); // 매치된 부분 가져오기
        hashtags.add(hashtag); // set에 저장
    }

    return hashtags;
}
```

### 도전과제 - 해시태그 검색
게시글 상세 페이지에서 해시태그 링크를 클릭하면 Get 방식으로 매핑된다.  
해당 태그를 포함하고 있는 게시글들을 articleService의 searchArticleByHashtag 메서드를 이용해 가져온다.
```java
// 해시태그를 이용한 검색
@GetMapping("/hashtag")
public String searchArticleByHashtag(
        @RequestParam("hashtag") String hashtag,
        Model model
) {
    model.addAttribute("hashtag", hashtag);
    model.addAttribute("articles", articleService.searchArticleByHashtag(hashtag));
    return "article/article-search-hashtag";
}
```
```java
// 해시태그를 이용해 게시글 검색
public List<Article> searchArticleByHashtag(String hashtag) {
    return articleRepository.findByHashtagsContainsOrderByIdDesc(hashtag);
}
```

### 도전 과제 - 게시글 검색
게시판의 게시글 리스트를 보는 화면에 게시글 검색을 위한 form을 추가한다.
```html
<form th:action="@{/boards/{boardId}/search(boardId=${board.id})}" method="get">
    <input type="text" name="search_text">
    <select name="search_opt">
        <option value="제목">제목</option>
        <option value="내용">내용</option>
    </select>
    <button type="submit">검색</button>
</form>
```
폼으로부터 Get 방식으로 검색어와 검색 기준을 받아 요청한다.
만약 검색 기준이 제목인지, 내용인지에 따라 articleService에 각기 다른 메서드를 요청해 게시글 검색 방식을 달리한다.
```java
// 특정 게시판 검색 결과
@GetMapping("/{boardId}/search")
public String boardSearch(
        @PathVariable("boardId") Long boardId,
        @RequestParam("search_text") String query,
        @RequestParam("search_opt") String opt,
        Model model
) {
    if (opt.equals("제목")) {
        model.addAttribute("articles", articleService.searchBoardArticleByTitle(boardId, query));
    } else {
        model.addAttribute("articles", articleService.searchBoardArticleByContent(boardId, query));
    }
    model.addAttribute("board", boardService.readBoard(boardId));
    model.addAttribute("search_text", query);
    model.addAttribute("search_opt", opt);
    return "board/board-search";
}
```
```java
// 제목을 기준으로 특정 게시판의 게시글 검색
public List<Article> searchBoardArticleByTitle(Long boardId, String title) {
    return articleRepository.findByBoardIdAndTitleContainsOrderByIdDesc(boardId, title);
}

// 내용을 기준으로 특정 게시판의 게시글 검색
public List<Article> searchBoardArticleByContent(Long boardId, String content) {
    return articleRepository.findByBoardIdAndContentContainsOrderByIdDesc(boardId, content);
}
```

### 도전 과제 - 게시글 이미지 추가
게시글에 이미지 업로드를 위한 폼을 추가한다.
```html
<!-- 이미지 업로드 폼 -->
<form th:action="@{/article/{id}/image/add(id=${article.id})}" method="post" enctype="multipart/form-data">
    이미지 추가: <input type="file" name="upload_image" accept="image/*"><br>
    비밀번호: <input type="password" name="password">
    <input type="submit" value="업로드">
</form>
```
PathVariable인 게시글 번호와, 폼으로부터 받은 image파일, password를 articleService의 saveArticleWithImage 메서드로 넘겨 이미지를 저장하고 성공 여부를 받아 성공 시 원래 게시글로, 실패 시 실패 화면으로 넘어간다.
```java
// 게시글에 이미지 추가 처리
@PostMapping("/{articleId}/image/add")
public String addImageToPost(
        @PathVariable("articleId") Long articleId,
        @RequestParam("upload_image") MultipartFile image,
        @RequestParam("password") String password,
        Model model
) throws IOException {
    // 이미지 추가 성공 시
    if (articleService.saveArticleWithImage(articleId, image, password)) {
        return "redirect:/article/" + articleId;
    // 실패 시
    } else {
        model.addAttribute("articleId", articleId);
        return "article/image-add-failed";
    }
}
```
articleId로 부터 article을 불러와 프로젝트 내부 resources/static/files에 저장한 이미지 이름을 article.images에 저장한다.
```java
// 게시글에 이미지 추가
public boolean saveArticleWithImage(Long articleId, MultipartFile file, String password) throws IOException {
    Article article = readArticle(articleId);
    if (article == null || !article.getPassword().equals(password)) {
        return false;
    }
    if (!file.isEmpty()) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = System.getProperty("user.dir") + "/src/main/resources/static/files/" + fileName;

        // 이미지를 서버에 저장
        File dest = new File(filePath);
        dest.mkdirs();
        file.transferTo(dest);

        // 이미지 경로를 Entity에 추가
        article.getImages().add(fileName);

        articleRepository.save(article);
        return true;
    }
    return false;
}
```

### 도전 과제 - 게시글 이미지 삭제
게시글의 이미지 옆 삭제 링크를 클릭하면 이미지 삭제폼으로 연결된다.
```java
// 게시글에 이미지 삭제 폼
@GetMapping("/{articleId}/image/delete")
public String deleteImageView(
        @PathVariable("articleId") Long articleId,
        @RequestParam("image") String image,
        Model model
) {
    model.addAttribute("articleId", articleId);
    model.addAttribute("image", image);
    return "article/image-delete";
}
```
폼으로부터 입력받은 password와 image 파일 이름으로 service의 deleteImageFromArticle 메서드를 호출해 성공 여부를 반환받는다. 
이미지 삭제 성공 시 기존 게시글로 이동하고, 삭제 실패 시 삭제 실패 안내페이지로 이동한다.
```java
// 이미지 삭제 처리
@PostMapping("/{articleId}/image/delete")
public String deleteImage(
        @PathVariable("articleId") Long articleId,
        @RequestParam("password") String password,
        @RequestParam("image") String image,
        Model model
) throws IOException {
    // 이미지 삭제 성공 시
    if (articleService.deleteImageFromArticle(articleId, password, image)) {
        return "redirect:/article/" + articleId;
    // 이미지 삭제 실패 시
    } else {
        model.addAttribute("articleId", articleId);
        return "article/image-delete-failed";
    }
}
```
service의 deleteImageFromArticle 메서드에서는 입력받은 articleId로 article을 불러와 게시글의 패스워드와 입력받은 패스워드가 동일한지 판단한 후 이미지 삭제를 진행하고 성공 여부를 반환한다.
```java
// 특정 게시글의 이미지 삭제
public boolean deleteImageFromArticle(Long articleId, String password, String image) throws IOException {
    Article article = readArticle(articleId);
    if (article == null || !article.getPassword().equals(password)) return false;
    List<String> images = article.getImages();
    images.remove(image);
    articleRepository.save(article);
    deleteFile(image);
    return true;
}

// 파일 삭제
public void deleteFile(String fileName) throws IOException {
    String filePath = System.getProperty("user.dir") + "/src/main/resources/static/files/" + fileName;
    Files.deleteIfExists(Paths.get(filePath));
}
```

### 도전 과제 - 이전글, 다음글
게시글 읽기 화면에서 해당 게시글의 이전글, 다음글을 service로부터 불러와 model에 추가해준다.
```java
// article 읽기 화면
@GetMapping("/{articleId}")
public String articleView(
        @PathVariable("articleId") Long id,
        Model model
) {
    model.addAttribute("article", articleService.readArticle(id));
    model.addAttribute("nextArticle", articleService.getNextArticle(id));
    model.addAttribute("prevArticle", articleService.getPrevArticle(id));
    return "article/article-view";
} 
```
service에서는 id로부터 article을 불러와 해당 게시판의 boardId를 알아낸다음 repository로부터 해당 게시판의 id가 id 다음으로 큰것, 다음으로 작은것을 반환한다.
```java
// 게시글의 특정 게시판 내부의 다음글 가져오기
public Article getNextArticle(Long articleId) {
    Article article = readArticle(articleId);
    Long boardId = article.getBoard().getId();
    return articleRepository.findTopByBoardIdAndIdLessThanOrderByIdDesc(boardId, articleId).orElse(null);
}

// 게시글의 특정 게시판 내부의 이전글 가져오기
public Article getPrevArticle(Long articleId) {
    Article article = readArticle(articleId);
    Long boardId = article.getBoard().getId();
    return articleRepository.findTopByBoardIdAndIdGreaterThan(boardId, articleId).orElse(null);
}
```
html에서는 이전글, 또는 다음글이 null이 아니라면 표시해주도록 한다.
```html
<!-- 이전글, 다음글 -->
<div th:if="${prevArticle != null}">
    <a th:href="@{/article/{id}(id=${prevArticle.id})}">이전글: [[${prevArticle.title}]]</a>
</div>
<div th:if="${nextArticle != null}">
    <a th:href="@{/article/{id}(id=${nextArticle.id})}">다음글: [[${nextArticle.title}]]</a>
</div>
```

## 어려웠던 점

- #### 전체 게시판과 다른 게시판들의 로직 분리
  - 전체 게시판은 다른 게시판과는 다르게 실제 하위 게시글을 가지는 것도 아니고, 게시글 보기, 이전글, 다음글, 검색 결과 등 에서 다른 방식으로 작동함
  - 현재 게시판이 전체 게시판인지 아니면 다른 게시판인지 로직을 구분시켜 작동할 방법을 생각하기 까다로웠음
  - 결국에는 엔드포인트에 차이를 둬서 서로 컨트롤러에서 매핑되는 메서드가 다르게 되도록 함

- #### 이미지 업로드 시 문제점
  - 이미지 업로드 시 resources/static/files 폴더내에 저장이 되기는 하나
  웹사이트에서는 해당 경로로 이미지 접속이 되지 않는 문제가 있었음
  - 인텔리제이를 클릭해서 한번 프로그램에 focus를 주니 프로젝트가 리빌드 되면서 그제서야 이미지로에 접속이 되는 것을 확인
  - 아직 근본적인 해결 방법을 찾지 못했다.


## 실행 및 테스트 방법
- 프로젝트를 clone시 db.sqlite 파일이 같이 받아지기 때문에 application.yaml에서 db관련 설정을 바꿀 필요는 없을것이라고 생각된다.
- 만약, db가 table이 없거나 테이블안에 내용물이 없을 경우 ddl-auto: create, sql.init.mode=always로 바꿔서 실행해보자.
- 이미지 업로드 테스트 시 업로드 파일이 프로젝트 파일에 바로 반영이 안되는 문제가 있어, 이미지 업로드 후 인텔리제이를 클릭해 프로그램에 focus를 줘서 프로그램이 리빌드되도록 하고 웹페이지를 새로고침하면 이미지가 정상적으로 출력된다.