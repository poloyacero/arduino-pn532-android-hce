package androidx.core.graphics;

import android.content.Context;
import android.graphics.Typeface;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import androidx.core.provider.FontsContractCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class TypefaceCompatApi21Impl extends TypefaceCompatBaseImpl {
    private static final String TAG = "TypefaceCompatApi21Impl";

    TypefaceCompatApi21Impl() {
    }

    private File getFile(ParcelFileDescriptor fd) {
        try {
            String path = Os.readlink("/proc/self/fd/" + fd.getFd());
            if (OsConstants.S_ISREG(Os.stat(path).st_mode)) {
                return new File(path);
            }
            return null;
        } catch (ErrnoException e) {
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0049, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x004e, code lost:
        r8 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x004f, code lost:
        r6.addSuppressed(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0052, code lost:
        throw r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0055, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0056, code lost:
        if (r3 != null) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x005c, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x005d, code lost:
        r4.addSuppressed(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0060, code lost:
        throw r5;
     */
    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    public Typeface createFromFontInfo(Context context, CancellationSignal cancellationSignal, FontsContractCompat.FontInfo[] fonts, int style) {
        if (fonts.length < 1) {
            return null;
        }
        FontsContractCompat.FontInfo bestFont = findBestInfo(fonts, style);
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(bestFont.getUri(), "r", cancellationSignal);
            File file = getFile(pfd);
            if (file != null) {
                if (file.canRead()) {
                    Typeface createFromFile = Typeface.createFromFile(file);
                    if (pfd != null) {
                        pfd.close();
                    }
                    return createFromFile;
                }
            }
            FileInputStream fis = new FileInputStream(pfd.getFileDescriptor());
            Typeface createFromInputStream = super.createFromInputStream(context, fis);
            fis.close();
            if (pfd != null) {
                pfd.close();
            }
            return createFromInputStream;
        } catch (IOException e) {
            return null;
        }
    }
}
