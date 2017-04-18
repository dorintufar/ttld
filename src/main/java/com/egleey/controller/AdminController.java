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

//    @RequestMapping(value="/audio/test",method=RequestMethod.GET)
//    public void playAudio(HttpServletRequest request, HttpServletResponse response){
//        String filename = "C:/temp/1.avi";
//        File outdir = new File("c:/temp/pictures");
//        IContainer container = IContainer.make();
//
//        if (container.open(filename, IContainer.Type.READ, null) < 0)
//            throw new IllegalArgumentException("could not open file: "
//                    + filename);
//        int numStreams = container.getNumStreams();
//        int videoStreamId = -1;
//        IStreamCoder videoCoder = null;
//
//        // нужно найти видео поток
//        for (int i = 0; i < numStreams; i++) {
//            IStream stream = container.getStream(i);
//            IStreamCoder coder = stream.getStreamCoder();
//            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
//                videoStreamId = i;
//                videoCoder = coder;
//                break;
//            }
//        }
//        if (videoStreamId == -1)
//            // кажись не нашли
//            throw new RuntimeException("could not find video stream in container: "
//                    + filename);
//
//        // пытаемся открыть кодек
//        if (videoCoder.open() < 0)
//            throw new RuntimeException(
//                    "could not open video decoder for container: " + filename);
//
//        IPacket packet = IPacket.make();
//        // с 3-ей по 5-ую микросекунду
//        long start = 6 * 1000 * 1000;
//        long end = 12 * 1000 * 1000;
//        // с разницей в 100 милисекунд
//        long step = 500 * 1000;
//
//        END: while (container.readNextPacket(packet) >= 0) {
//            if (packet.getStreamIndex() == videoStreamId) {
//                IVideoPicture picture = IVideoPicture.make(
//                        videoCoder.getPixelType(), videoCoder.getWidth(),
//                        videoCoder.getHeight());
//                int offset = 0;
//                while (offset < packet.getSize()) {
//                    int bytesDecoded = videoCoder.decodeVideo(picture, packet,
//                            offset);
//                    // Если что-то пошло не так
//                    if (bytesDecoded < 0)
//                        throw new RuntimeException("got error decoding video in: "
//                                + filename);
//                    offset += bytesDecoded;
//                    // В общем случае, нужно будет использовать Resampler. См.
//                    // tutorials!
//                    if (picture.isComplete()) {
//                        IVideoPicture newPic = picture;
//                        // в микросекундах
//                        long timestamp = picture.getTimeStamp();
//                        if (timestamp > start) {
//                            // Получаем стандартный BufferedImage
//                            BufferedImage javaImage = Utils
//                                    .videoPictureToImage(newPic);
//                            String fileName = String.format("%07d.png",
//                                    timestamp);
//                            ImageIO.write(javaImage, "PNG", new File(outdir,
//                                    fileName));
//                            start += step;
//                        }
//                        if (timestamp > end) {
//                            break END;
//                        }
//                    }
//                }
//            }
//        }
//        if (videoCoder != null) {
//            videoCoder.close();
//            videoCoder = null;
//        }
//        if (container != null) {
//            container.close();
//            container = null;
//        }
//
//    }
}
