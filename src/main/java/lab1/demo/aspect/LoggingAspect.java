package lab1.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {


    @Before("execution(* lab1.demo.service..*(..))")
    public void logBeforeService(JoinPoint joinPoint) {
        System.out.println("[LOG] Вызов метода: " + joinPoint.getSignature().getName()
                + " с аргументами: " + Arrays.toString(joinPoint.getArgs()));
    }


    @AfterReturning(pointcut = "execution(* lab1.demo.service..*(..))", returning = "result")
    public void logAfterService(JoinPoint joinPoint, Object result) {
        System.out.println("[LOG] Метод завершён: " + joinPoint.getSignature().getName()
                + " | Результат: " + result);
    }


    @AfterThrowing(pointcut = "execution(* lab1.demo.service..*(..))", throwing = "ex")
    public void logServiceExceptions(JoinPoint joinPoint, Throwable ex) {
        System.err.println("[ERROR] Ошибка в методе: " + joinPoint.getSignature().getName()
                + " | Сообщение: " + ex.getMessage());
    }
}
