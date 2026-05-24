package com.talentboozt.s_backend.domains.edu.seo.video;

import org.springframework.stereotype.Service;

/**
 * High-Performance YouTube Embed Optimizer.
 * Employs standard HTML5 "srcdoc" attributes to defer iframe script executions,
 * protecting mobile LCP (Largest Contentful Paint) performance scores during Googlebot audits.
 */
@Service
public class YoutubeEmbedOptimizer {

    /**
     * Generates a lightweight, lazy-loading iframe embedding structure.
     */
    public String generateLazyLoadEmbedMarkup(String videoId, String title) {
        if (videoId == null) return "";
        String normalizedTitle = (title != null) ? title : "Educational revision guides";
        
        return """
        <div class="youtube-lazy-embed" data-video-id="%s" style="position:relative;padding-bottom:56.25%%;height:0;overflow:hidden;border-radius:12px;">
          <iframe srcdoc="<style>*{padding:0;margin:0;overflow:hidden}html,body{height:100%%}img,span{position:absolute;width:100%%;top:0;bottom:0;margin:auto}span{height:1.5em;font:20px/1.5 sans-serif;text-align:center;color:white;text-shadow:0 0 0.5em black}</style><a href=https://www.youtube.com/embed/%s?autoplay=1><img src=https://img.youtube.com/vi/%s/hqdefault.jpg alt='%s'><span>▶</span></a>" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen title="%s" style="position:absolute;width:100%%;height:100%%;left:0;top:0;"></iframe>
        </div>
        """.formatted(videoId, videoId, videoId, normalizedTitle, normalizedTitle);
    }
}
