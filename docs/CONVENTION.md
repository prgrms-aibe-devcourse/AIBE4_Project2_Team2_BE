# 코딩 컨벤션

## 1. 네이밍 규칙

### 1.1 패키지
- 소문자만 사용
- 단어 구분은 점(.)으로
```java
kr.java.majormate.domain.member
kr.java.majormate.global.config
```

### 1.2 클래스/인터페이스
- PascalCase 사용
- 명사 사용
```java
MemberController
MemberService
MemberRepository
```

### 1.3 메서드
- camelCase 사용
- 동사로 시작
```java
getMember()
createMember()
updateMember()
deleteMember()
```

### 1.4 변수
- camelCase 사용
- 의미 있는 이름 사용
```java
Member member
String memberName
List<Member> memberList
```

### 1.5 상수
- UPPER_SNAKE_CASE 사용
```java
public static final int MAX_RETRY_COUNT = 3;
public static final String DEFAULT_ENCODING = "UTF-8";
```

---

## 2. 코드 스타일

### 2.1 들여쓰기
- 스페이스 4칸 사용
- 탭 사용 금지

### 2.2 중괄호
- K&R 스타일 사용
```java
// Good
public void method() {
    if (condition) {
        doSomething();
    }
}

// Bad
public void method()
{
    if (condition)
    {
        doSomething();
    }
}
```

### 2.3 줄 길이
- 최대 120자
- 120자를 초과하면 줄바꿈

### 2.4 메서드 길이
- 최대 50줄
- 50줄을 초과하면 리팩토링 고려

---

## 3. 레이어별 규칙

### 3.1 Controller
```java
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> getMember(@PathVariable Long id) {
        return ApiResponse.success(memberService.getMember(id));
    }
}
```

**규칙**
- `@RestController` 사용
- `@RequestMapping`으로 기본 경로 설정
- `@RequiredArgsConstructor`로 의존성 주입
- 비즈니스 로직은 Service에 위임
- 응답은 `ApiResponse`로 통일

### 3.2 Service
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse createMember(MemberCreateRequest request) {
        // 비즈니스 로직
    }
}
```

**규칙**
- `@Service` 사용
- `@Transactional(readOnly = true)` 클래스 레벨에 선언
- CUD 메서드에는 `@Transactional` 추가
- 예외는 Custom Exception 사용

### 3.3 Repository
```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.memberType = :memberType")
    List<Member> findByMemberType(@Param("memberType") MemberType memberType);
}
```

**규칙**
- `JpaRepository` 상속
- 메서드명은 Spring Data JPA 규칙 따름
- 복잡한 쿼리는 `@Query` 사용

### 3.4 Entity
```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // 생성자
    @Builder
    private Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // 비즈니스 메서드
    public void updateName(String name) {
        this.name = name;
    }
}
```

**규칙**
- `@Entity` 사용
- `@Getter`만 사용 (`@Setter` 금지)
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- 생성은 `@Builder` 또는 정적 팩토리 메서드
- 수정은 비즈니스 메서드 제공
- `BaseEntity` 상속으로 createdAt, updatedAt 자동 관리

### 3.5 DTO
```java
// Request
public record MemberCreateRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String name
) {
}

// Response
public record MemberResponse(
    Long id,
    String email,
    String name,
    MemberType memberType
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getMemberType()
        );
    }
}
```

**규칙**
- Java 17+ `record` 사용
- Request DTO에는 Validation 어노테이션 추가
- Response DTO에는 `from()` 정적 팩토리 메서드 제공

---

## 4. Exception 처리

### 4.1 Custom Exception
```java
public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
```

### 4.2 사용 예시
```java
Member member = memberRepository.findById(id)
    .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
```

---

## 5. Git 컨벤션

### 5.1 브랜치 전략
```
main: 운영 브랜치
develop: 개발 브랜치
feature/기능명: 기능 개발 브랜치
hotfix/버그명: 긴급 수정 브랜치
```

### 5.2 커밋 메시지
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅, 세미콜론 누락 등
refactor: 코드 리팩토링
test: 테스트 코드 추가
chore: 빌드 설정, 패키지 매니저 수정
```

**예시**
```
feat: 회원 가입 기능 추가
fix: 로그인 시 NPE 발생 버그 수정
docs: API 명세서 업데이트
refactor: MemberService 리팩토링
```

### 5.3 Pull Request
- 제목: `[기능명] 작업 내용`
- 본문: 변경 사항, 테스트 방법 작성
- 리뷰어 최소 1명 이상 승인 후 merge

---

## 6. 테스트 코드

### 6.1 테스트 메서드명
```java
@Test
void getMember_Success() {
    // given
    Long memberId = 1L;

    // when
    MemberResponse response = memberService.getMember(memberId);

    // then
    assertThat(response).isNotNull();
}

@Test
void getMember_NotFound_ThrowsException() {
    // given
    Long invalidId = 999L;

    // when & then
    assertThatThrownBy(() -> memberService.getMember(invalidId))
        .isInstanceOf(NotFoundException.class);
}
```

**규칙**
- 메서드명: `메서드명_조건_결과`
- Given-When-Then 패턴 사용
- AssertJ 사용

---

## 7. 주석

### 7.1 클래스 주석
```java
/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
public class MemberService {
}
```

### 7.2 메서드 주석
- 메서드명으로 의도가 명확하면 주석 불필요
- 복잡한 비즈니스 로직에만 주석 추가

### 7.3 인라인 주석
- 코드로 설명 가능하면 주석 대신 변수/메서드 추출
- 불가피한 경우에만 사용

---

## 8. 기타 규칙

### 8.1 매직 넘버 금지
```java
// Bad
if (status == 1) {
}

// Good
public static final int STATUS_ACTIVE = 1;
if (status == STATUS_ACTIVE) {
}

// Better
public enum Status {
    ACTIVE, INACTIVE
}
if (status == Status.ACTIVE) {
}
```

### 8.2 메서드 파라미터
- 최대 3개까지
- 3개 초과 시 DTO/VO 사용

### 8.3 Optional 사용
- 반환 타입으로만 사용
- 필드, 파라미터로 사용 금지

```java
// Good
public Optional<Member> findMember(Long id) {
}

// Bad
public void method(Optional<Member> member) {
}
```
