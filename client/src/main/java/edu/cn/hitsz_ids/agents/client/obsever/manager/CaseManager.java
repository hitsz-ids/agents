package edu.cn.hitsz_ids.agents.client.obsever.manager;

import edu.cn.hitsz_ids.agents.client.obsever.response.DataCase;
import edu.cn.hitsz_ids.agents.client.obsever.observer.Observer;

import java.io.IOException;
import java.util.*;

public class CaseManager {
    private final Observer observer;
    private final List<CaseMessage> list = new ArrayList<>();

    public CaseManager(Observer observer) {
        this.observer = observer;
    }

    public <T extends DataCase<R, D>, R, D> D await(T t) throws IOException {
        try {
            var message = new CaseMessage(t);
            list.add(message);
            return t.await(observer, message.getCaseId());
        } catch (IOException e) {
            observer.error(e);
            throw e;
        }
    }

    private CaseMessage getMessage(String id) {
        var index = 0;
        CaseMessage caseMessage = null;
        for (CaseMessage message : list) {
            if (Objects.equals(message.getCaseId(), id)) {
                caseMessage = message;
                break;
            }
            index++;
        }
        if (!Objects.isNull(caseMessage)) {
            list.remove(index);
        }
        return caseMessage;
    }

    public void single(String id, Object data) {
        var message = getMessage(id);
        if (Objects.isNull(message)) {
            return;
        }
        message.getDataCase().single(data);
    }

    public void singleAll() {
        for (var message : list) {
            message.getDataCase().single(null);
        }
    }

    public void destroy() {
        singleAll();
        list.clear();
    }
}
