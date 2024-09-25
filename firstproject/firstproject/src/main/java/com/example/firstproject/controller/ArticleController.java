package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
public class ArticleController {

    @Autowired//스프링 부트가 미리 생성해놓은 객체를 가져다가 자동 연결
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm(){
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form){

        //System.out.println(form.toString());->로깅 기능으로 대체!
        log.info(form.toString());

        //1.dto를 변환 entity
        Article article=form.toEntity();
       // System.out.println(article.toString());
        log.info(article.toString());
        //repository에게 entity를 db안에 저장하게 함
        Article saved=articleRepository.save(article);
        //System.out.println(saved.toString());
        log.info(saved.toString());
        return "redirect:/articles/" + saved.getId();
    }

    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model){
        log.info("id = "+id);
        //1. id로 데이터를 가져옴
        Article articleEntity=articleRepository.findById(id).orElse(null);
        //2. 가져온 데이터를 모델에 등록
        model.addAttribute("article", articleEntity);
        //3. 보여줄 페이지를 설정
        return "articles/show";
    }

    @GetMapping("/articles")
    public String index(Model model){
        //1. 모든 article을 가져온다
        List<Article> articleEntityList=articleRepository.findAll();
        //2. 가져온 article묶음을 뷰로 전달
        model.addAttribute("articleList", articleEntityList);

        //3. 뷰페이지를 설정
        return "articles/index"; //articles.index mustache
    }
    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model){
        //수정할 데이터 가져오기
        Article articleEntity=articleRepository.findById(id).orElse(null);
        //모델에 데이터 등록하기
        model.addAttribute("article", articleEntity);
        //뷰페이지 설정하기

        return "articles/edit";
    }
    @PostMapping("/articles/update")
    public String update(ArticleForm form){
        log.info(form.toString());
        //dto를 엔티티로 변환하기
        Article articleEntity=form.toEntity();
        log.info(articleEntity.toString());
        //엔티티를 db에 저장하기
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);
        if(target!=null){
            articleRepository.save(articleEntity);
        }

        //수정 결과 페이지로 리다이렉트하기
        return "redirect:/articles/" + articleEntity.getId();
    }
    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr){
        log.info("삭제 요청이 들어왔습니다!");
        //1. 삭제 대상을 가져온다
        Article target=articleRepository.findById(id).orElse(null);
        log.info(target.toString());
        //2. 그 대상을 삭제한다
        if(target !=null){
            articleRepository.delete(target);
            rttr.addFlashAttribute("msg", "삭제가 완료되었습니다.");
        }
        //3. 결과 페이지로 리다이렉트한다
        return "redirect:/articles";
    }

}
