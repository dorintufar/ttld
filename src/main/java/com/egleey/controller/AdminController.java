package com.egleey.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static com.egleey.service.webradio.AudioDirectoryWatcher.AUDIO_DIRECTORY_ROOT;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final String TEMPLATE_PAGE_DASHBOARD = "/admin/page/dashboard.html.twig";
    private static final String TEMPLATE_PAGE_PLAYER = "/admin/page/player.html.twig";

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ModelAndView dashboard() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName(TEMPLATE_PAGE_DASHBOARD);

        return mav;
    }

    @RequestMapping(value = "/player", method = RequestMethod.GET)
    public ModelAndView player() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName(TEMPLATE_PAGE_PLAYER);

        return mav;
    }

    @RequestMapping(value = "/upload/sound", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadSound(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) {
        String fileName;

        if (!file.isEmpty()) {
            try {
                fileName = file.getOriginalFilename();
                String fullPath = String.format("%s%s%s%s%s", AUDIO_DIRECTORY_ROOT, File.separator,
                        path, File.separator, fileName);
                byte[] bytes = file.getBytes();
                BufferedOutputStream buffStream =
                        new BufferedOutputStream(new FileOutputStream(new File(fullPath)));
                buffStream.write(bytes);
                buffStream.close();
            } catch (Exception e) {
                return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping("/directory/create")
    @ResponseBody
    public ResponseEntity<String> directoryCreate(@RequestParam("path") String path) throws IOException {
        String fullPath = String.format("%s%s%s", AUDIO_DIRECTORY_ROOT, File.separator, path);

        File file = new File(fullPath);
        if (!file.mkdirs()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
