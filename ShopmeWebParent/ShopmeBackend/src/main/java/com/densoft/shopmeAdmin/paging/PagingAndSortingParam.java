package com.densoft.shopmeAdmin.paging;

import java.lang.annotation.Retention;;
import java.lang.annotation.Target;
import java.net.PortUnreachableException;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface PagingAndSortingParam {

    String moduleURL();

    String listName();
}
