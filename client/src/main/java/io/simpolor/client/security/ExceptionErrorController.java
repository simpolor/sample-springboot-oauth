package io.simpolor.client.security;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

@Controller
public class ExceptionErrorController implements ErrorController {

    // 에러 페이지 정의
    private final String ERROR_404_PATH = "/error/404";
    private final String ERROR_500_PATH = "/error/500";
    private final String ERROR_PATH = "/error/error";

    @GetMapping(value = "/error")
    public ModelAndView handleError(ModelAndView mav, HttpServletRequest request) {

        // 에러 코드를 획득한다.
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // 에러 코드에 대한 상태 정보

        if(Objects.nonNull(status)){

            HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));
            int statusCode = Integer.valueOf(httpStatus.value());

            if(HttpStatus.NOT_FOUND.value() == statusCode){

                mav.addObject("code", status.toString());
                mav.addObject("msg", httpStatus.getReasonPhrase());
                mav.addObject("timestamp", LocalDateTime.now());

                mav.setViewName(ERROR_404_PATH);
                return mav;

            }else if(HttpStatus.INTERNAL_SERVER_ERROR.value() == statusCode){

                mav.setViewName(ERROR_500_PATH);
                return mav;
            }
        }

        mav.setViewName(ERROR_PATH);
        return mav;
    }


    @Override
    public String getErrorPath() {
        return "/error";
    }
}
