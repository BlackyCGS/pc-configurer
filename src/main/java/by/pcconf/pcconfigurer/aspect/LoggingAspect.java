package by.pcconf.pcconfigurer.aspect;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {

  private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("within(by.pcconf.pcconfigurer.controller.*)")
  public void controllerPointcut() {}

  @Pointcut("within(by.pcconf.pcconfigurer.service.*)")
  public void servicePointcut() {}

  @Before("controllerPointcut()")
  public void logBefore(JoinPoint joinPoint) {
    HttpServletRequest request = ((ServletRequestAttributes)
            Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    logger.info("Endpoint call: [{}] {} - {}", request.getMethod(),
            request.getRequestURI(), joinPoint.getSignature());
  }

  @AfterReturning("servicePointcut()")
  public void logAfterReturning(JoinPoint joinPoint) {
    HttpServletRequest request = ((ServletRequestAttributes)
            Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    logger.info("Service return: [{}] {} - {}", request.getMethod(),
            request.getRequestURI(), joinPoint.getSignature());
  }

}