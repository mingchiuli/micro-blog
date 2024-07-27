package org.chiu.micro.blog.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.chiu.micro.blog.lang.SensitiveTypeEnum;
import org.chiu.micro.blog.lang.StatusEnum;
import org.chiu.micro.blog.req.BlogEditPushAllReq;
import org.chiu.micro.blog.req.SensitiveContentReq;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.stream.Collectors;



public class PushAllConstraintValidator implements ConstraintValidator<PushAllValue, BlogEditPushAllReq> {

    private static final Pattern pattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    @Override
    public boolean isValid(BlogEditPushAllReq blog, ConstraintValidatorContext context) {

        if (Objects.isNull(blog.getTitle())) {
            return false;
        }

        if (Objects.isNull(blog.getDescription())) {
            return false;
        }

        if (Objects.isNull(blog.getContent())) {
            return false;
        }

        if (Objects.isNull(blog.getVersion())) {
            return false;
        }

        Integer status = blog.getStatus();

        Set<Integer> statusSet = Arrays.stream(StatusEnum.values())
                .map(StatusEnum::getCode)
                .collect(Collectors.toSet());
        
        if (!statusSet.contains(status)) {
            return false;
        }

        if (!pattern.matcher(blog.getLink()).matches()) {
            return false;
        }

        List<SensitiveContentReq> sensitiveContentList = blog.getSensitiveContentList();

        Set<Integer> sensitiveSet = Arrays.stream(SensitiveTypeEnum.values())
                .map(SensitiveTypeEnum::getCode)
                .collect(Collectors.toSet());
        for (var sensitive : sensitiveContentList) {
            if (Objects.isNull(sensitive.getStartIndex())) {
                return false;
            }

            if (!StringUtils.hasLength(sensitive.getContent())) {
                return false;
            }

            if (!sensitiveSet.contains(sensitive.getType())) {
                return false;
            }
        }

        return true;
    }
}
