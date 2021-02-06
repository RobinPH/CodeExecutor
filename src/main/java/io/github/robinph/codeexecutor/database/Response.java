package io.github.robinph.codeexecutor.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Response<T> {
    @RequiredArgsConstructor
    public enum Code {
        SUCCESS(200),
        DOES_EXIST(201),
        UNKNOWN_ERROR(400),
        DOES_NOT_EXIST(401),
        ALREADY_EXISTS(402);

        private @Getter
        final int number;
    }

    private @Getter final Code code;
    private @Getter T content;
    private @Getter String message;

    public Response(Code code) {
        this.code = code;
    }

    public Response(Code code, T content, String message) {
        this.code = code;
        this.content = content;
        this.message = message;
    }

    public boolean isSuccess() {
        return this.code.getNumber() >= 200 && this.code.getNumber() <= 299;
    }

    public boolean isError() {
        return this.code.getNumber() >= 400 && this.code.getNumber() <= 499;
    }
}
