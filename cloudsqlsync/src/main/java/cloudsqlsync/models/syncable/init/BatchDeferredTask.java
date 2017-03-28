package cloudsqlsync.models.syncable.init;

import cloudsqlsync.models.syncable.SynchronizationHelper;
import cloudsqlsync.models.syncable.Version;
import cloudsqlsync.utils.SqlConnection;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectModel;
import io.yawp.repository.query.NoResultException;

import java.util.List;

import static io.yawp.repository.Yawp.feature;

public class BatchDeferredTask implements DeferredTask {

    private List<? extends IdRef<?>> ids;

    public BatchDeferredTask(List<? extends IdRef<?>> ids) {
        this.ids = ids;
    }

    @Override
    public void run() {
        SynchronizationHelper helper = feature(SynchronizationHelper.class);
        SqlConnection conn = SqlConnection.newInstance(true);

        for (IdRef<?> id : ids) {
            if (versionAlreadyExists(id)) {
                continue;
            }
            syncrhonizeEntity(conn, id, helper);
        }
    }

    private void
    syncrhonizeEntity(SqlConnection conn, IdRef<?> id, SynchronizationHelper helper) {
        try {
            ObjectModel model = new ObjectModel(id.getClazz());
            Object entity = id.fetch();

            helper.createTableIfNotExists(conn, model.getKind());
            helper.updateEntity(conn, model.getKind(), id.getUri(), JsonUtils.to(entity), 1l);
            helper.saveVersionMarker(entity);

        } catch (NoResultException e) {
        }
    }

    private boolean versionAlreadyExists(IdRef<?> id) {
        IdRef<Version> versionId = id.createChildId(Version.class, 1l);
        try {
            versionId.fetch();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

}
