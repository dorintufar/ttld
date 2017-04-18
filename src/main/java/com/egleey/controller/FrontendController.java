package com.egleey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("")
public class FrontendController {
    @RequestMapping(value = "/web/radio", method = RequestMethod.GET)
    public ModelAndView webRadio() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("frontend/page/radio.html.twig");

        return mav;
    }
}
