package com.artineer.spring_lecture_week_2.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j // 로깅을 하기 위해 로깅 라이브러리
@Component // 빈으로 등록
@Aspect // 흩어진 기능을 뭉쳐서 쓴다. AOP로 동작
public class ExecuteLogAspect {
    @SuppressWarnings("unchecked")
    @Around(value = "@annotation(ExecuteLog)") // 언제 실행되는지(위빙되는 시점) @annotation(ExecuteLog)가 선언된 함수에서만 위빙이 일어난다.
    public <T> Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        // jointpoint는 위빙된 위치에 대한 정보를 갖고 있다.

        // 작업 시작 시간을 구합니다.
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 원본함수의 시그니처를 joinpoint를 통해 가지고 온다.

        // ExecuteLog 어노테이션에 type 값에 들어간 타입을 추론합니다.
        Class<T> clazzType = this.classType(signature.getMethod().getAnnotations());

        // 위빙된 객체의 작업을 진행합니다.
        final T result = (T) joinPoint.proceed(); // joinPoint.proceed()는 원본 객체를 실행하라는 의미
        // 어떤 타입인지 알아냈기 때문에 원하는 타입으로 받아낼 수 있다.
        // 받아냈기 때문에 타입이 가지고 있는 필드랑 메소드에 접근이 가능하다.

        String methodName = signature.getName(); // 어떤 함수가 실행된지 모르기 때문에 이름을 가져온다.
        String input = Arrays.toString(signature.getParameterNames()) + Arrays.toString(joinPoint.getArgs()); // 입력에 해당하는 값

        String output = this.toString(result);

        log.info("Method Name : {}, Input : {}, Output : {}, Execute Time : {}", methodName, input, output, (System.currentTimeMillis() - start) + " ms");

        return result;
    }

    // final T result = (T) joinPoint.proceed();을 리스트업 하는 함수
    // toString에 해당하는 함수를 직접 구현.
    private <T> String toString(T result) throws Throwable {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(Field field : result.getClass().getDeclaredFields()) { /* 타입의 field를 받아서 모두 보여주는 함수
                                                        (여기서는 Article의 Long id, String title, String content) */
            if(Strings.isBlank(field.getName())) { // field 네임이 없으면 기록하지 않는다.
                continue;
            }
            field.setAccessible(true); // field는 보통 정보은닉(private)을 해놓지만(여기선 하지 않았음) public으로 바꿔즌다
            sb.append(field.getName()).append("=").append(field.get(result)).append(", ");
        }
        sb.append("]");

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> classType(Annotation[] annotations) throws Throwable { /* ExecuteLogAspect는 어떤 타입이 넘어온지 모른다.
                                                                                어떠한 타입이 들어와도 사용할 수 있도록 제네릭 타입을 사용하여 구현한다.*/
        Annotation executeLogAnnotation = Arrays.stream(annotations) // ExecuteLog 어노테이션을 찾아야 한다.
                .filter(a -> a.annotationType().getCanonicalName().equals(ExecuteLog.class.getCanonicalName()))
                .findFirst().orElseThrow(() -> new RuntimeException("ExecuteLog Annotation is not existed..."));
        // 필터링해서 ExecuteLog와 이름이 동일한 것 하나만 가지고 오고(리턴시키고) 없다면 예외를 던진다.

        // ExecuteLog 어노테이션을 가지고 왔고, <?>의 정보를 얻기 위해 ExecuteLog의 type()메소드에 접근하여 리턴 타입을 받는다.
        String typeMethodName = "type"; // 접근하려는 메소드의 이름
        Method typeMethod = Arrays.stream(executeLogAnnotation.annotationType().getDeclaredMethods())
                .filter(m -> m.getName().equals(typeMethodName))
                .findFirst().orElseThrow(() -> new RuntimeException("type() of ExecuteLog is not existed..."));
        /*여러 개의 함수를 가질 수 있기 때문에 열거형으로 작성
         메소드들 중 메소드의 이름이 type이라는 메소드 하나만 리턴한다. 없다면 예외를 던진다.*/
        return (Class<T>) typeMethod.invoke(executeLogAnnotation);
        // invoke를 하면 클래스 타입으로 인스턴스를 넣어 메소드가 실행될 수 있게 만들어준다.
        // 우리가 원하는 T 타입(여기선 Article)이 리턴된다.
    }
}
