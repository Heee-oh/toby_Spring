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

## AOP

### 다이내믹 프록시와 팩토리 빈
프록시 : 자신이 클라이언트가 사용하려고 하는 실제 대상인 것처럼 위장해서 클라이언트의 요청을 받아주는 것
타겟 : 실제 오브젝트

프록시 특징 : 타깃과 같은 인터페이스를 구현, 프록시가 타깃을 제어할 수 있는 위치


프록시 사용 목적
1. 클라이언트가 타깃에 접근하는 방법을 제어
2. 타깃에 부가적인 기능을 부여


#### 데코레이션 패턴
타깃에 부가적인 기능을 런타임시 다이내믹하게 부여해주기 위해 프록시를 사용하는 패턴

다이내믹하게 기능을 부가한다 : 컴파일시점, 즉 코드상에서는 어떤 방법과 순서로 프록시와 타깃이 연결되어 사용되는지 정해져 있지 않다.
말그대로 데코레이션이라 여러 프록시를 사용 가능 (인터페이스 기반)
DI 주입을 잘 설정


#### 프록시 패턴
프록시 vs 프록시 패턴
프록시 : 클라이언트와 사용자 사이에 대리 역할 오브젝트
프록시 패턴 : 타깃에 대한 접근 방법을 제어하려는 목적


프록시 패턴의 프록시는 타깃의 기능을 확장, 추가하지 않음
대신 클라이언트가 타깃에 접근하는 방식을 변경해줌


타깃 오브젝트를 생성하기가 복잡하거나 당장 필요없는 경우 꼭 필요한 시점까지 오브젝트 생성을 하지 않는 편이 좋다.
타깃 오브젝트에 대한 레퍼런스가 미리 필요할 경우
프록시 패턴을 적용

클라이언트에게 타깃에 대한 레퍼런스를 넘길때 실제 타깃 오브젝트 대신 프록시를 넘기고
메서드를 통해 타깃을 사용하려고 시도하면 그때 프록시가 타깃 오브젝트를 생성하고 요청을 위임


접근 제어 어떻게?
프록시에서 특정 메서드는 사용 불가능하게 구현해서 예외 던지기

#### 다이내믹 프록시
매번 새롭게 클래스 정의해서 위임하는게 쉽지 않음
-> reflect 패키지 사용하여 프록시를 다이내믹하게 생성

프록시의 구성과 프록시 작성의 문제점
프록시는 2가지 기능으로 구성
1. 타깃과 같은 메서드를 구현하고, 메서드 호출시 타깃 오브젝트로 위임
2. 지정된 요청에 대해서는 부가기능을 수행


#### 리플렉션
자바의 코드 자체를 추상화해서 접근하도록 만든 것

리플렉션은 자바에서 실행 중인 객체의 클래스 정보, 메서드, 필드 등에 접근하고
이를 동적으로 호출하거나 조작할 수 있게 해주는 기능

모든 자바 클래스는 클래스 자체의 구성정보를 담은 Class 타입의 오브젝트를 하나씩 가짐
.class, 객체가 있다면 .getClass()로 Class 오브젝트를 얻고 
이를 통해 메타정보를 가져오거나 조작 할 수 있음

```java
import java.lang.reflect.Method;

Method length = String.class.getMethod("length");
```

reflect의 Method 인터페이스는 메서드에 대한 자세한 정보 뿐만 아니라
이를 이용해 특정 오브젝트의 메서드를 실행가능

invoke() 
```java
    // obj : 호출할 객체, args : 파라미터 순서대로 넣기
    public Object invoke(Object obj, Object... args)
        throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException
    {
    }
    
```

#### 다이내믹 프록시 적용
프록시 팩토리에 의해 런타임 시 다이내믹하게 만들어지는 오브젝트
인터페이스를 모두 구현한 클래스를 생성해줌

다이내믹 프록시 -> InvocationHandler -> Target
```java
public interface InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
    
}
```

리플렉션 메서드인 invoke를 이용해 타깃 오브젝트의 메서드를 호출할 때 타깃 오브젝트에서 발생하는 예외가
InvocationTargetException으로 한 번 포장돼서 전달되기에
InvocationTargetException으로 잡아서 e.getTargetException() 메서드로 중첩되어 있는 예외를 가져온다.


```java

        try {

            Object result = method.invoke(target, args);
            transactionManager.commit(status);
            return result;
            
        } catch (InvocationTargetException e) {
            transactionManager.rollback(status);
            throw e.getTargetException();
        }
        
```

### 중간 정리
클라이언트 -> 다이나믹 프록시 -> InvocationHandler(부가기능) ->  Reflection(method.invoke()) -> target

리플렉션은 클래스의 메타정보와 실행등을 담당, 
InvocationHandler에서는 부가기능을 붙여주고 실질적으로는 Reflect의 Method.invoke()로 실제 타깃의 메서드를 호출함



#### 다이나믹 프록시를 위한 팩토리 빈
다이나믹 프록시 오브젝트는 일반적인 스프링 빈 등록 방법이 없다.

스프링 빈은 기본적으로 클래스 이름과 프로퍼티로 정의됨
스프링은 지정된 클래스 이름을 갖고 리플렉션을 이용 -> 해당 클래스의 오브젝트 생성

```java
Date now = (Date)Class.forName("java.util.Date").newInstance();
// 9버전부터 Deprecated 되었다.
@CallerSensitive
@Deprecated(since="9")
public T newInstance(){}

// 대신 이걸 쓰라고 나와있음
Date now = (Date)Class.forName("java.util.Date").getDeclaredConstructor().newInstance();

```
스프링은 내부적으로 리플렉션 API를 이용해서 빈 정의에 나오는 클래스 이름을 가지고 빈 오브젝트를 생성
문제는 다이나믹 프록시 오브젝트는 이런식으로 프록시 오브젝트 생성되지 않음
다이나믹하게 새로 정의되서 사용하기에 클래스 정보를 미리 알아낼 방법이 없음

다이나믹 프록시는 Proxy 클래스의 newProxyInstance() 라는 스태틱 팩토리 메서드를 통해서만 생성가능

##### 팩토리 빈
스프링을 대신해서 오브젝트의 생성로직을 담당하도록 만들어진 특별한 빈
가장 간단한 방법은 FactoryBean 인터페이스를 구현

> 리플렉션은 private 로 선언된 접근규약을 위반할 수 있는 강력한 기능이 있다. 
> 
> 하지만 private로 선언한 이유가 있기때문에 권장하지 않음, 위험함

```java
public interface FactoryBean<T> { // T에는 실제 빈에 등록할 오브젝트 타입 넣기 (제네릭)
    String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";

    //빈 오브젝트 생성 및 반환
    @Nullable
    T getObject() throws Exception;
    
    // 생성 오브젝트 타입 반환
    @Nullable
    Class<?> getObjectType();

    // 생성 오브젝트가 항상 싱글톤인지 
    default boolean isSingleton() {
        return true;
    }
}
```

FactoryBean을 구현한 특정 오브젝트 팩토리빈을 
스프링 빈으로 등록시 스프링이 getObject를 호출하여 
반환한 오브젝트를 스프링 빈으로 등록함


이걸 이용해서 다이나믹 프록시를 getObject에서 생성하고 반환하면 
다이나믹 프록시도 스프링 빈에 등록되고 DI가 가능해짐

참고로
context.getBean("%BeanName) 으로 가져올 시 팩토리빈이 반환됨
TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);


프록시 팩토리 빈의 한계
한 클래스 안에서의 메서드들의 부가기능은 가능하지만 여러 클래스를 대상으로는 불가능
여러 개의 부가기능 적용시 부가기능 개수만큼 프록시 팩토리 빈 설정이 늘어남
TransactionHandler가 프록시 팩토리 빈 개수만큼 만들어짐


### 스프링이 제공하는 프록시 팩토리 빈
MethodInterceptor를 이용하여 advice를 설정(부가기능)
pointcut으로 메서드 선정알고리즘 지정

advisor = pointcut + advice

```java
import org.springframework.aop.framework.ProxyFactoryBean;

ProxyFactoryBean pfbean = new ProxyFactoryBean(); // 프록시 팩토리 빈 생성
pfbean.setTarget(); // 타겟 지정 
pfbean.addAdvisor(); // 어드바이저 등록

```
위 처럼 프록시 팩토리빈에 타겟을 지정해주면 해당 타겟에 맞는 다이나믹 프록시를 생성해줌
해당 다이나믹 프록시는 지정된 advisor(or advice)에 위임함
advice 즉, MethodInterceptor를 구현한 부가기능 클래스는 
프록시 팩토리빈 생성시 넣은 타겟이 만든 MethodInvocation 객체를 통해서 
실제 타겟의 메서드 실행가능

즉, advice는 타겟을 알 필요가 없어져서 부가기능에 더 집중가능
단지 MethodInvocation.procced 를 호출하고 앞뒤로 부가기능을 붙임




