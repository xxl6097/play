package org.videolan.libvlc;

/**
 * Java/JNI wrapper for the libmedia_media_list_t structure.
 */
public class MediaList {
    private static final String TAG = "MEDIA/LibMEDIA/MediaList";

    private long mMediaListInstance = 0; // Read-only, reserved for JNI
    private long mEventHanderGlobalRef = 0; // Read-only, reserved for JNI
    private LibVLC mLibMEDIA; // Used to create new objects that require a lib instance
    private boolean destroyed = false;
    private EventHandler mEventHandler;

    public MediaList(LibVLC libMEDIA) {
        mEventHandler = new EventHandler(); // used in init() below to fire events at the correct targets
        mMediaListInstance = init(libMEDIA);
        mLibMEDIA = libMEDIA;
    }
    private native long init(LibVLC libmedia_instance);

    @Override
    public void finalize() {
        if(!destroyed) destroy();
    }

    /**
     * Releases the media list.
     *
     * The object should be considered released after this and must not be used.
     */
    public void destroy() {
        nativeDestroy();
        mMediaListInstance = 0;
        mEventHanderGlobalRef = 0;
        mLibMEDIA = null;
        destroyed = true;
    }
    private native void nativeDestroy();

    public void add(String mrl) {
        add(mLibMEDIA, mrl, false, false);
    }
    public void add(String mrl, boolean noVideo) {
        add(mLibMEDIA, mrl, noVideo, false);
    }
    private native void add(LibVLC libmedia_instance, String mrl, boolean noVideo, boolean noOmx);

    /**
     * Clear the media list. (remove all media)
     */
    public native void clear();

    /**
     * This function checks the currently playing media for subitems at the given
     * position, and if any exist, it will expand them at the same position
     * and replace the current media.
     *
     * @param position The position to expand
     * @return -1 if no subitems were found, 0 if subitems were expanded
     */
    public int expandMedia(int position) {
        return expandMedia(mLibMEDIA, position);
    }
    private native int expandMedia(LibVLC libmedia_instance, int position);

    public void loadPlaylist(String mrl) {
        loadPlaylist(mLibMEDIA, mrl);
    }
    private native void loadPlaylist(LibVLC libmedia_instance, String mrl);

    public void insert(int position, String mrl) {
        insert(mLibMEDIA, position, mrl);
    }
    private native void insert(LibVLC libmedia_instance, int position, String mrl);

    public native void remove(int position);

    public native int size();

    /**
     * @param position The index of the media in the list
     * @return null if not found
     */
    public native String getMRL(int position);

    public EventHandler getEventHandler() {
        return mEventHandler;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LibMEDIA Media List: {");
        for(int i = 0; i < size(); i++) {
            sb.append(((Integer)i).toString());
            sb.append(": ");
            sb.append(getMRL(i));
            sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }
}
