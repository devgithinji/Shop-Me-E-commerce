package com.densoft.shopmeAdmin.order;

import com.densoft.shopmeAdmin.paging.PagingAndSortingHelper;
import com.densoft.shopmecommon.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final int ORDERS_PER_PAGE = 10;

    @Autowired
    private OrderRepository repo;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyWord();

        Sort sort = null;

        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        Page<Order> page = null;

        if (keyword != null) {
            page = repo.findAll(keyword, pageable);
        } else {
            page = repo.findAll(pageable);
        }

        helper.updateModelAttributes(pageNum, page);
    }
}
