package com.sikuinnova.canvachallenge.presentation.exception;

import android.content.Context;

import com.sikuinnova.canvachallenge.R;
import com.sikuinnova.canvachallenge.data.exception.NetworkConnectionException;
import com.sikuinnova.canvachallenge.domain.exception.ImageNotFoundException;

/**
 * Created by josearce on 7/2/17.
 */
public class ErrorMessageFactory {

    private ErrorMessageFactory() {
        //empty
    }

    public static String create(Context context, Exception exception) {
        String message = context.getString(R.string.exception_message_generic);

        if (exception instanceof NetworkConnectionException) {
            message = context.getString(R.string.exception_message_no_connection);
        } else if (exception instanceof ImageNotFoundException) {
            message = context.getString(R.string.exception_message_image_not_found);
        }

        return message;
    }

}
