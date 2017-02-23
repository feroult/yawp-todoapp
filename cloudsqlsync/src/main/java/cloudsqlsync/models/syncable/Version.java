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

}
