package com.code.server.login.action;
import com.code.server.login.service.RecommendDelegateService;
import com.code.server.login.vo.RecommandUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dajuejinxian on 2018/5/16.
 */
@RestController

@RequestMapping("/recommandDelegate")
public class RecommendDelegateAction {

    @Autowired
    private RecommendDelegateService recommendDelegateService;

//    @RequestParam("/findUser")
//    public RecommandUserVo findDelegate(@RequestParam("userId") long userId){
//
//        RecommandUserVo recommandUserVo = recommendDelegateService.findRecommandUser(userId);
//
//        return recommandUserVo;
//    }

}
