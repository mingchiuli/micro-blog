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

    READ_TOKEN("read_token:"),

    BLOG_STATUS("blog_status"),

    CONSUME_MONITOR("consume:"),

    BLOOM_FILTER_BLOG("bloom_filter_blog"),

    BLOOM_FILTER_BLOG_STATUS("bloom_filter_blog_status"),

    BLOOM_FILTER_PAGE("bloom_filter_page"),

    BLOOM_FILTER_YEAR_PAGE("bloom_filter_page_"),

    BLOOM_FILTER_YEARS("bloom_filter_years");



    private final String info;

}

