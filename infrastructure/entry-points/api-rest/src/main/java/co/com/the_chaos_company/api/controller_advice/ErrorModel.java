package co.com.the_chaos_company.api.controller_advice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorModel {
    private String exception;
    private String message;
}
