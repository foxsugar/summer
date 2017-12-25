package com.code.server.login.service;

import com.code.server.db.model.Charge;


public interface PayService {
    Charge create(Integer money);
}
