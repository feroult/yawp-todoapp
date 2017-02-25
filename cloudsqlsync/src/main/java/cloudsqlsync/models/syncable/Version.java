package cloudsqlsync.models.syncable;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

import java.io.Serializable;

@Endpoint(path = "/sync-versions")
public class Version implements Serializable {

    @Id
    IdRef<Version> id;

    @ParentId
    IdRef<?> entityId;

    Long version = 0l;

    public void setId(IdRef<Version> id) {
        this.id = id;
    }

    public void setEntityId(IdRef<?> entityId) {
        this.entityId = entityId;
    }

    public Long getVersion() {
        return version;
    }

    public String getEntityUri() {
        return entityId.getUri();
    }

    public String getEntityKind() {
        return entityId.getModel().getKind();
    }

    public void inc() {
        version++;
    }
}
