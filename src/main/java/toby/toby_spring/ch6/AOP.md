DI와 객체지향의 다형성을 이용한 트랜잭션파트와 비지니스 파트 분리

단위 테스트 : 테스트 대상 클래스를 목 옵젝 등의 테스트 대역을 이용해 의존 옵젝, 외부 리소스 사용하지 않도록 고립시켜 테스트하는 것
통합 테스트 : 두개 이상의 성격이나 계층이 다른 옵젝 연동, 외부 DB나 파일, 서비스 등의 리소스 참여하는 테스트

### 테스트 가이드 라인

1. 항상 단위 테스트를 먼저 고려
2. 외부 리소스 사용시 통합테스트
3. DAO는 DB를 통해 로직 수행 -> 고립된 테스트 힘듬 -> 따라서 통합테스트 , 물론 단위 테스트 가능(목 옵젝)
4. 의존관계 필요 -> 통합테스트
5. 단위 테스트 만들기가 너무 복잡하다 -> 첨부터 통합테스트 고려
6. 스프링 이용한 추상적 레벨의 테스트시 스프링 테스트 컨텍스트 프레임워크 이용해 통합 테스트


### 목 프레임워크
단위 테스트시 목 오브젝트 만들기가 쉽지 않다. (많으면 많을수록)
또한 메소드 별로 다른 검증 필요시 같은 의존 인터페이스를 구현한 여러 개의 목 클래스를 선언해야함

이러한 번거로움을 해결해줄 목 오브젝트 지원 프레임워크가 있다.
#### Mockito 프레임 워크
간단한 메서드로 다이내믹하게 특정 인터페이스를 구현한 목 오브젝트 생성가능

Mockito를 사용하는 방법은 이것 말고도 @ExtendWith(MockitoExtension.class) 을 테스트 클래스에 붙이는 방법도 있다.

이 책에서는 모키토 패키지를 static으로 임포트해서 간단하게 사용중이다. 따라서 
자세히 적자면 

```java
import org.mockito.Mockito;

UserDao mock = Mockito.mock(UserDao.class);
Mockito.verify(mock, Mockito.times(2)).update(Mockito.any(User.class));

```

```java
UserDao userDao = Mock.mock(UserDao.class);
```

목 오브젝트를 만들었으면 메서드 호출시 사용자 목록을 리턴하도록 스텁 기능을 추가 
즉, when(특정 메서드 호출시). thenReturn(이 값을 리턴하라)
```java
when(mockUserDao.getAll()).thenReturn(users);
```

검증 파트 
verify(목옵젝, [호출에 대한 검증 각종 메서드들]).호출메서드(인자);
```java
verify(mockUserDao, times(2)).update(any(User.class)); // any(파라미터X) 사용시 파라미터 내용 무시하고, 호출 횟수만 확인 가능
```

주의사항
```java
// X 에러 발생 (하나는 일반 값, 하나는 매처)
verify(mock).save(user, anyString()); 

// O 올바른 예시 (둘 다 매처 사용)
verify(mock).save(eq(user), anyString());
```

Mockito 목 오브젝트는 4단계를 거쳐 사용
1. 인터페이스 이용해 목 옵젝 생성
2. 목 옵젝 리턴 값 있다면 이를 지정, 메서드 호출시 예외를 강제로 던지게 가능
3. 테스트 대상 옵젝에 DI해서 목 옵젝 테스트중 사용되도록 만듬
4. 테스트 대상 옵젝을 사용한 후 목 옵젝의 특정 메서드가 호출됐는지, 어떤 값을 가지고 몇 번 호출됐는지 검증


Mockito는 목 옵젝의 테스트는 가능하지만 실질적인 파라미터나 결과값에 대한 검증은 불가능 
따라서 Junit이나 Assert를 이용해서 검증
```java
Mockito.verify(mock).update(users.get(3)); // users.get(3)의 레벨이 실제 변경되었는지는 검증 불가, 단지 update 파라미터로 넘겨진 적만 있음
Assertions.assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);
```

#### ArgumentCaptor
책에 자세히 소개가 안된거같은데 직접 정리해서 넣음

Mockito에서 메서드 호출시 인자를 낚아채서 저장하는 도구

**사용하는 경우**
메서드 내부에서 객체 수정되어 전달
메서드 내부에서 new 로 생성된 객체가 인자로 넘어감

사용방법
```java

// 캡터 객체 생성
ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(검증 하려는 객체 타입.class);

// 캡처 수행
verify(테스트 대상).호출메서드(userCaptor.capture());

// getValue()로 실제 전달된객체 꺼내와서 검증
User user = userCaptor.getValue();
assertThat(user.getLevel()).isEqualTo(Level.SILVER);
```
