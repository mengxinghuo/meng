package com.truck.controller.backend;

import com.github.pagehelper.PageInfo;
import com.truck.common.ServerResponse;
import com.truck.pojo.Project;
import com.truck.service.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/manage/project/")
public class ProjectController {

    @Autowired
    private IProjectService iProjectService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageNum",defaultValue = "10") int pageSize,
                                         Integer customerId){
        return iProjectService.listByCustomerId(customerId,pageNum,pageSize);
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Project project) {
        return iProjectService.add(project);
    }



    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpSession session, Integer id) {
        return iProjectService.del(id);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Project project) {
        return iProjectService.update(project);
    }


}