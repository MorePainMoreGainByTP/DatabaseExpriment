package com.example.swjtu.databaseexpriment.exercise8.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/6/1.
 */

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    private static final String TAG = "MyPrintDocumentAdapter";

    private Context context;
    private PrintedPdfDocument mPdfDocument;    //pdf 文件
    private List<List<String>> contents;

    private int totalPages; //总的页数
    private int itemsPerPage = 10;
    private final int topMargin = 50, leftMargin = 10, titleTopMargin = 40;

    public MyPrintDocumentAdapter(Context context, List<List<String>> contents) {
        this.context = context;
        this.contents = contents;
    }

    /**
     * 用于设置将要打印的页面数
     * including the number of pages and content type.
     */
    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        mPdfDocument = new PrintedPdfDocument(context, newAttributes);
        // Respond to cancellation request
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }
        // Compute the expected number of printed pages
        int pages = totalPages = computePageCount(newAttributes);

        if (pages > 0) {
            // Return print information to print framework
            PrintDocumentInfo info = new PrintDocumentInfo
                    .Builder("print_book_restore.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(pages)
                    .build();
            // Content layout reflow is complete
            callback.onLayoutFinished(info, true);
        } else {
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("Page count calculation failed.");
        }
    }

    private int computePageCount(PrintAttributes printAttributes) {
        //int itemsPerPage = 4; // default item count for portrait mode

        PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
        if (!pageSize.isPortrait()) {
            // Six items per page in landscape orientation
            itemsPerPage = 14;
        }

        // Determine number of print items
        int printItemCount = contents.size();

        return (int) Math.ceil(1.0 * printItemCount / itemsPerPage);
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        // Iterate over each page of the document,
        // check if it's in the output range.
        for (int i = 0; i < totalPages; i++) {
            // Check to see if this page is in the output range.
            PdfDocument.Page page = mPdfDocument.startPage(i);
            // check for cancellation
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                mPdfDocument.close();
                mPdfDocument = null;
                return;
            }
            // Draw page content for printing
            drawPage(page, i * itemsPerPage);
            // Rendering is complete, so page can be finalized.
            mPdfDocument.finishPage(page);
//
//            if (containsPage(pageRanges, i)) {
//                // If so, add it to writtenPagesArray. writtenPagesArray.size()
//                // is used to compute the next output page index.
//                writtenPagesArray.append(writtenPagesArray.size(), i);
//            }
        }

        // Write PDF document to file
        try {
            mPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            mPdfDocument.close();
            mPdfDocument = null;
        }
        // PageRange[] writtenPages = computeWrittenPages();
        // Signal the print framework the document is complete
        callback.onWriteFinished(pageRanges);
    }

    private void drawPage(PdfDocument.Page page, int index) {
        Canvas canvas = page.getCanvas();
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (index == 0) {//绘制标题
            Paint paint = new Paint();
            paint.setTextSize(18);
            paint.setColor(Color.BLACK);
            String title = "图书库存一览表";
            int textWidth = (int) paint.measureText(title);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            int textHeight = (int) (Math.ceil(fontMetrics.descent - fontMetrics.top) + 2);
            canvas.drawText(title, width / 2 - textWidth / 2, titleTopMargin, paint);

            Date date = new Date(System.currentTimeMillis());
            String strDate = "报表生成日期：" + date.toString();
            paint.setTextSize(8);
            canvas.drawText(strDate, width - leftMargin - paint.measureText(strDate), titleTopMargin + textHeight, paint);
        }
        // Log.i(TAG, " height:" + height + ",width:" + width);
        // Log.i(TAG, " MaximumBitmapHeight:" + canvas.getMaximumBitmapHeight() + ",MaximumBitmapWidth:" + canvas.getMaximumBitmapWidth());
        drawRow(canvas, topMargin + 50, 50, new ArrayList<String>(), true);
        int high = 0;
        if (index + itemsPerPage >= contents.size()) {
            for (int i = 0; i < contents.size() - index; i++) {
                drawRow(canvas, topMargin + 50 * (i + 2), 50, contents.get(index + i), false);
                high = topMargin + 50 * (i + 3);
            }
            drawFooter(canvas, index + 1, high);
        } else {
            for (int i = 0; i < itemsPerPage; i++) {
                drawRow(canvas, topMargin + 50 * (i + 2), 50, contents.get(index + i), false);
                high = topMargin + 50 * (i + 3);
            }
            drawFooter(canvas, index + 1, high);
        }


        // units are in points (1/72 of an inch)
        /*
        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(36);
        canvas.drawText("Test Title", leftMargin, titleBaseLine, paint);

        paint.setTextSize(11);
        canvas.drawText("Test paragraph", leftMargin, titleBaseLine + 25, paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect(100, 100, 172, 172, paint);
        */
    }

    private void drawRow(Canvas canvas, int height, int spec, List<String> text, boolean isHeader) {
        Log.i(TAG, "drawRectAndText: " + text);
        if (isHeader) {
            text = new ArrayList<>();
            text.add("书号");
            text.add("书名");
            text.add("作者");
            text.add("图书分类");
            text.add("开本");
            text.add("库存数");
            text.add("单价");
            text.add("总码样");
        }

        int width = (canvas.getWidth() - 2 * leftMargin) / 22;
        Paint paint = new Paint();
        paint.setTextSize(8);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        int lastWidth = 0;
        for (int i = 0; i < text.size(); i++) {
            int scale = 1;
            switch (i) {
                case 0:
                    scale = 5;
                    break;
                case 1:
                    scale = 5;
                    break;
                case 2:
                    scale = 2;
                    break;
                case 3:
                    scale = 3;
                    break;
                case 4:
                    scale = 1;
                    break;
                case 5:
                    scale = 2;
                    break;
                case 6:
                    scale = 2;
                    break;
                case 7:
                    scale = 2;
                    break;
            }

            if (i == 0) {
                lastWidth = leftMargin;
            }
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(lastWidth, height, lastWidth + scale * width, height + spec, paint);
            paint.setStyle(Paint.Style.FILL);
            int yOffset = 0;
            if (i == 5 || i == 6 || i == 7) {
                paint.setTextAlign(Paint.Align.RIGHT);
                yOffset = scale * width - 5;
            } else {
                paint.setTextAlign(Paint.Align.LEFT);
                yOffset = width / 5;
            }
            canvas.drawText(text.get(i), lastWidth + yOffset, height + spec / 2, paint);
            //canvas.drawText(text.get(i), lastWidth + width / 2, height + spec / 2,,,paint);
            lastWidth = lastWidth + scale * width;
        }

    }

    private void drawFooter(Canvas canvas, int index, int high) {
        Paint paint = new Paint();
        paint.setTextSize(11);
        if (index == totalPages) {
            String str = "制表人（签字）：                                                  审核人（签字）：";
            canvas.drawText(str, leftMargin, high + 20, paint);
        }
        String bottomStr = "第" + index + "页/共" + totalPages + "页";
        canvas.drawText(bottomStr, canvas.getWidth() / 2 - (int) (paint.measureText(bottomStr) / 2), canvas.getHeight() - 5, paint);
    }

}
