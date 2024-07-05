package org.chiu.micro.blog.lang;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mingchiuli
 * @create 2021-12-14 11:58 AM
 */
@Getter
@AllArgsConstructor
public enum Const {

    A_WEEK("604899"),

    HOT_READ("hot_read"),

    QUERY_DELETED("del_blog_user:"),

    READ_TOKEN("read_token:");


    private final String info;

}

