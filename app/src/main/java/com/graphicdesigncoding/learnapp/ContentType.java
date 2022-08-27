package com.graphicdesigncoding.learnapp;
//COPYRIGHT BY GraphicDesignCoding
public enum ContentType {
    //IMAGE TYPES///////////////////////////////////////////////////////////////////////////////////
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    IMAGE_BMP("image/bmp"),
    IMAGE_AVIV("image/avif"),
    IMAGE_ICO("image/vnd.microsoft.icon"),
    IMAGE_SVG_XML("image/svg+xml"),
    IMAGE_TIF_TIFF("image/tiff"),
    IMAGE_WEBP("image/webp"),
    //VIDEO TYPES///////////////////////////////////////////////////////////////////////////////////
    VIDEO_AVI("video/x-msvideo"),
    VIDEO_MP4("video/mp4"),
    VIDEO_MPEG("video/mpeg"),
    VIDEO_OGV("video/ogg"),
    VIDEO_TS("video/mp2t"),
    VIDEO_WEBM("video/webm"),
    VIDEO_3GP("video/mp2t"),
    //AUDIO TYPES///////////////////////////////////////////////////////////////////////////////////
    AUDIO_AAC("audio/aac"),
    AUDIO_MID("audio/midi"),
    AUDIO_MIDI("audio/x-midi"),
    AUDIO_MPEG("audio/mpeg"),
    AUDIO_OGG("audio/ogg"),
    AUDIO_OPUS("audio/opus"),
    AUDIO_WEBA("audio/webm"),
    //TEXT TYPES////////////////////////////////////////////////////////////////////////////////////
    TEXT_CSS("text/css"),
    TEXT_CSV("text/csv"),
    TEXT_HTML("text/html"),
    TEXT_ICS("text/calendar"),
    TEXT_JS("text/javascript"),
    TEXT_PLAIN("text/plain"),
    //APPLICATION TYPES/////////////////////////////////////////////////////////////////////////////
    APPLICATION_7Z("application/x-7z-compressed"),
    APPLICATION_ZIP("application/zip"),
    APPLICATION_XUL("application/vnd.mozilla.xul+xml"),
    APPLICATION_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    APPLICATION_XLS("application/vnd.ms-excel"),
    APPLICATION_XHTML("application/xhtml+xml"),
    APPLICATION_VSD("application/vnd.visio"),
    APPLICATION_TAR("application/x-tar"),
    APPLICATION_SWF("application/x-shockwave-flash"),
    APPLICATION_SH("application/x-sh"),
    APPLICATION_RTF("application/rtf"),
    APPLICATION_RAR("application/vnd.rar"),
    APPLICATION_PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    APPLICATION_PPT("application/vnd.ms-powerpoint"),
    APPLICATION_PHP("application/x-httpd-php"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_OGX("application/ogg"),
    APPLICATION_ODT("application/vnd.oasis.opendocument.text"),
    APPLICATION_ODS("application/vnd.oasis.opendocument.spreadsheet"),
    APPLICATION_ODP("application/vnd.oasis.opendocument.presentation"),
    APPLICATION_MPKG("application/vnd.apple.installer+xml"),
    APPLICATION_JSONLD("application/ld+json"),
    APPLICATION_JSON("application/json"),
    APPLICATION_JAR("application/java-archive"),
    APPLICATION_GZ("application/gzip"),
    APPLICATION_EPUB("application/epub+zip"),
    APPLICATION_EOT("application/vnd.ms-fontobject"),
    APPLICATION_BZ("application/x-bzip"),
    APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    APPLICATION_DOC("application/msword"),
    APPLICATION_CSH("application/x-csh"),
    APPLICATION_CDA("application/x-cdf"),
    APPLICATION_BZ2("application/x-bzip2"),
    APPLICATION_BIN("application/octet-stream"),
    APPLICATION_AZW("application/vnd.amazon.ebook"),
    APPLICATION_ARC("application/x-freearc"),
    APPLICATION_ABW("application/x-abiword"),
    //FONT TYPES////////////////////////////////////////////////////////////////////////////////////
    FONT_OTF("font/otf"),
    FONT_TTF("font/ttf"),
    FONT_WOFF("font/woff"),
    FONT_WOFF2("font/woff2");


    // declaring private variable for getting values
    private final String action;

    // getter method
    public String getAction()
    {
        return this.action;
    }

    // enum constructor - cannot be public or protected
    ContentType(String action)
    {
        this.action = action;
    }
}
