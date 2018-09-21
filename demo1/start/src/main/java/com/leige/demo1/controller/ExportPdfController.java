package com.leige.demo1.controller;

import com.leige.demo1.service.ExportPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * @author peichanglei
 * @date 2018/9/22 11:10
 */
@RequestMapping("/export")
@Controller
public class ExportPdfController {
    @Autowired
    ExportPdfService exportPdfService;


    @RequestMapping(value = "/getPdf",method = {RequestMethod.GET,RequestMethod.POST},produces = "application/json")
    public void getPdf() {
        exportPdfService.exportPdf();
    }
}
