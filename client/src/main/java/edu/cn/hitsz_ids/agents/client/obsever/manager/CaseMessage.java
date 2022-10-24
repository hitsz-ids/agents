package edu.cn.hitsz_ids.agents.client.obsever.manager;

import edu.cn.hitsz_ids.agents.client.obsever.response.DataCase;

import java.util.Objects;
import java.util.UUID;

public class CaseMessage {
    private final String caseId;
    private final DataCase<?, ?> dataCase;

    public CaseMessage(DataCase<?, ?> dataCase) {
        this.dataCase = dataCase;
        caseId = UUID.randomUUID().toString();
    }

    public String getCaseId() {
        return caseId;
    }

    public <R> DataCase<?, R> getDataCase() {
        return (DataCase<?, R>) dataCase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var message = (CaseMessage) o;
        return caseId.equals(message.caseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseId);
    }
}
