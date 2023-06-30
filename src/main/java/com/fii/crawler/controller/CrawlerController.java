package com.fii.crawler.controller;

import com.fii.crawler.entities.FII;
import com.fii.crawler.service.CrawlerService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = "/fiiCrawler")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlerController {
    private CrawlerService crawlerService;
    @Autowired
    public CrawlerController(CrawlerService crawlerService){
        this.crawlerService = crawlerService;
    }

    @GetMapping(path = "/getFii")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FII> getFii(@RequestParam String code) throws IOException{
        FII fii = this.crawlerService.getDataFromFii(code);
        return new ResponseEntity<>(fii, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public String return500(IOException e){
        return e.getMessage();
    }
}
