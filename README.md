# 익명 의견 교환 웹페이지

## 구현 방법

### Entity - Article
```Java
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