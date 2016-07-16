package jp.co.tabocom.senjuconverter.worker;

/**
 * Workerの戻りオブジェクト
 * 
 * @author turbou
 * 
 */
public class WorkerRtn {
    /* リターンコード(今のところWorkerRtnEnum.SUCCESS or WorkerRtnEnum.FAILURE) */
    private WorkerRtnEnum code;
    /* なんかしらのメッセージ(今のところExceptionのgetMessage()) */
    private String message;
    /* 例外が発生した場合のStackTrace */
    private String stackTrace;

    public WorkerRtn() {
        // とりあえずは処理成功をデフォルトとしておく。
        this.code = WorkerRtnEnum.SUCCESS;
    }

    public void setCode(WorkerRtnEnum code) {
        this.code = code;
    }

    public WorkerRtnEnum getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

}
