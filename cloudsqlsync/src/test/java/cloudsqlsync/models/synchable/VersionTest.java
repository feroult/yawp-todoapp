package cloudsqlsync.models.version;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cloudsqlsync.utils.EndpointTestCase;

public class VersionTest extends EndpointTestCase {

    @Test
    public void testCreate() {
        // TODO Auto-generated method stub
        String json = post("/versions", "{}");
        Version version = from(json, Version.class);

        assertNotNull(version);
    }

}
