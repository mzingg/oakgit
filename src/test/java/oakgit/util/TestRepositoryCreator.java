package oakgit.util;

import com.adobe.granite.repository.impl.CommitStats;
import com.adobe.granite.repository.impl.GraniteContent;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.jackrabbit.oak.InitialContent;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.api.ContentSession;
import org.apache.jackrabbit.oak.api.Root;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.namepath.NamePathMapper;
import org.apache.jackrabbit.oak.plugins.atomic.AtomicCounterEditorProvider;
import org.apache.jackrabbit.oak.plugins.commit.ConflictValidatorProvider;
import org.apache.jackrabbit.oak.plugins.commit.JcrConflictHandler;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.bundlor.BundlingConfigInitializer;
import org.apache.jackrabbit.oak.plugins.index.WhiteboardIndexEditorProvider;
import org.apache.jackrabbit.oak.plugins.name.NameValidatorProvider;
import org.apache.jackrabbit.oak.plugins.name.NamespaceEditorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.TypeEditorProvider;
import org.apache.jackrabbit.oak.plugins.observation.ChangeCollectorProvider;
import org.apache.jackrabbit.oak.plugins.observation.CommitRateLimiter;
import org.apache.jackrabbit.oak.plugins.tree.impl.RootProviderService;
import org.apache.jackrabbit.oak.plugins.tree.impl.TreeProviderService;
import org.apache.jackrabbit.oak.plugins.version.VersionHook;
import org.apache.jackrabbit.oak.security.authentication.AuthenticationConfigurationImpl;
import org.apache.jackrabbit.oak.security.authentication.token.TokenConfigurationImpl;
import org.apache.jackrabbit.oak.security.authorization.AuthorizationConfigurationImpl;
import org.apache.jackrabbit.oak.security.internal.TestSecurityProvider;
import org.apache.jackrabbit.oak.security.principal.PrincipalConfigurationImpl;
import org.apache.jackrabbit.oak.security.privilege.PrivilegeConfigurationImpl;
import org.apache.jackrabbit.oak.security.user.UserConfigurationImpl;
import org.apache.jackrabbit.oak.spi.commit.EditorProvider;
import org.apache.jackrabbit.oak.spi.query.WhiteboardIndexProvider;
import org.apache.jackrabbit.oak.spi.security.SecurityProvider;
import org.apache.jackrabbit.oak.spi.security.authentication.SystemSubject;
import org.apache.jackrabbit.oak.spi.security.authorization.AuthorizationConfiguration;
import org.apache.jackrabbit.oak.spi.security.principal.EveryonePrincipal;
import org.apache.jackrabbit.oak.spi.security.user.AuthorizableType;
import org.apache.jackrabbit.oak.spi.security.user.UserConfiguration;
import org.apache.jackrabbit.oak.spi.security.user.util.UserUtil;
import org.apache.jackrabbit.oak.spi.whiteboard.DefaultWhiteboard;
import org.apache.jackrabbit.oak.spi.whiteboard.Whiteboard;

import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;
import javax.security.auth.Subject;
import java.io.IOException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class TestRepositoryCreator {

  private final DocumentNodeStore nodeStore;
  private final SecurityProvider securityProvider;
  private final Whiteboard whiteboard;
  private EditorProvider changeCollectorProvider;
  private final CommitStats commitStats;
  private final CommitRateLimiter commitRateLimiter;
  private int observationQueueLength;
  private final WhiteboardIndexProvider indexProvider;
  private final WhiteboardIndexEditorProvider indexEditorProvider;
  private boolean fastQueryResultSize;

  public TestRepositoryCreator(DocumentNodeStore nodeStore) {
    this.nodeStore = nodeStore;
    this.whiteboard = new DefaultWhiteboard();
    this.changeCollectorProvider = new ChangeCollectorProvider();
    this.commitStats = new CommitStats();
    this.commitRateLimiter = new CommitRateLimiter();
    this.observationQueueLength = 1000;
    this.indexProvider = new WhiteboardIndexProvider();
    this.indexEditorProvider = new WhiteboardIndexEditorProvider();
    this.fastQueryResultSize = true;

    TestSecurityProvider testSecurityProvider = new TestSecurityProvider();

    RootProviderService rootProvider = new RootProviderService();
    TreeProviderService treeProvider = new TreeProviderService();

    UserConfigurationImpl userConfiguration = new UserConfigurationImpl();
    userConfiguration.setSecurityProvider(testSecurityProvider);
    userConfiguration.setRootProvider(rootProvider);
    userConfiguration.setTreeProvider(treeProvider);

    PrivilegeConfigurationImpl privilegeConfiguration = new PrivilegeConfigurationImpl();
    privilegeConfiguration.setSecurityProvider(testSecurityProvider);
    privilegeConfiguration.setRootProvider(rootProvider);
    privilegeConfiguration.setTreeProvider(treeProvider);

    AuthorizationConfigurationImpl authorizationConfiguration = new AuthorizationConfigurationImpl();
    authorizationConfiguration.setSecurityProvider(testSecurityProvider);
    authorizationConfiguration.setRootProvider(rootProvider);
    authorizationConfiguration.setTreeProvider(treeProvider);

    TokenConfigurationImpl tokenConfiguration = new TokenConfigurationImpl();
    tokenConfiguration.setSecurityProvider(testSecurityProvider);
    tokenConfiguration.setRootProvider(rootProvider);
    tokenConfiguration.setTreeProvider(treeProvider);

    AuthenticationConfigurationImpl authenticationConfiguration = new AuthenticationConfigurationImpl();
    authenticationConfiguration.setSecurityProvider(testSecurityProvider);
    authenticationConfiguration.setRootProvider(rootProvider);
    authenticationConfiguration.setTreeProvider(treeProvider);

    PrincipalConfigurationImpl principalConfiguration = new PrincipalConfigurationImpl();
    principalConfiguration.setSecurityProvider(testSecurityProvider);
    principalConfiguration.setRootProvider(rootProvider);
    principalConfiguration.setTreeProvider(treeProvider);

    testSecurityProvider.setWhiteboard(whiteboard);
    testSecurityProvider.setUserConfiguration(userConfiguration);
    testSecurityProvider.setAuthenticationConfiguration(authenticationConfiguration);
    testSecurityProvider.setAuthorizationConfiguration(authorizationConfiguration);
    testSecurityProvider.setPrincipalConfiguration(principalConfiguration);
    testSecurityProvider.setPrivilegeConfiguration(privilegeConfiguration);
    testSecurityProvider.setTokenConfiguration(tokenConfiguration);
    this.securityProvider = testSecurityProvider;
  }

  public JackrabbitRepository create() {
    Oak oak = new Oak(nodeStore)
        .withFailOnMissingIndexProvider();

    Jcr jcr = new Jcr(oak, false)
        .with(Runnable::run)
        .with(whiteboard)
        .with(new InitialContent())
        .with(createGraniteContent())
        .with(JcrConflictHandler.createJcrConflictHandler())
        .with(new VersionHook())
        .with(securityProvider)
        .with(new NameValidatorProvider())
        .with(new NamespaceEditorProvider())
        .with(new TypeEditorProvider())
        .with(new ConflictValidatorProvider())
        .with(new AtomicCounterEditorProvider());

    if (this.changeCollectorProvider != null) {
      jcr.with(this.changeCollectorProvider);
    }

    jcr.with(this.indexProvider)
        .with(this.indexEditorProvider)
        .with("crx.default")
        .withFastQueryResultSize(this.fastQueryResultSize);

    if (this.observationQueueLength > 0) {
      jcr.withObservationQueueLength(this.observationQueueLength);
    }

    if (this.commitStats != null) {
      jcr.with(this.commitStats);
    }

    if (this.commitRateLimiter != null) {
      jcr.with(this.commitRateLimiter);
    }

    jcr.with(BundlingConfigInitializer.INSTANCE);
    ContentRepository contentRepository = jcr.createContentRepository();
    setupPermissions(contentRepository, securityProvider);
//    if (this.componentContext != null) {
//      this.oakRepositoryRegistration = this.componentContext.getBundleContext().registerService(ContentRepository.class, contentRepository, (Dictionary) null);
//    }

    return (JackrabbitRepository) jcr.createRepository();
  }

  private GraniteContent createGraniteContent() {
    GraniteContent gc = new GraniteContent(true);
    String userRoot = UserUtil.getAuthorizableRootPath(securityProvider.getConfiguration(UserConfiguration.class).getParameters(), AuthorizableType.USER);
    gc.setUserHomePath(userRoot);
    return gc;
  }

  private static void setupPermissions(final ContentRepository repo, SecurityProvider securityProvider) {
    try (ContentSession contentSession = Subject.doAsPrivileged(
        SystemSubject.INSTANCE,
        (PrivilegedExceptionAction<ContentSession>) () -> repo.login(null, null),
        null
    )) {
      Root root = contentSession.getLatestRoot();
      AuthorizationConfiguration config = securityProvider.getConfiguration(AuthorizationConfiguration.class);
      AccessControlManager acMgr = config.getAccessControlManager(root, NamePathMapper.DEFAULT);
      setupPolicy("/oak:index", acMgr);
      setupPolicy("/jcr:system", acMgr);
      if (root.hasPendingChanges()) {
        root.commit();
      }
    } catch (RepositoryException | CommitFailedException | PrivilegedActionException | IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private static void setupPolicy(String path, AccessControlManager acMgr) throws RepositoryException {
    AccessControlPolicyIterator it = acMgr.getApplicablePolicies(path);

    while (it.hasNext()) {
      AccessControlPolicy policy = it.nextAccessControlPolicy();
      if (policy instanceof JackrabbitAccessControlList) {
        JackrabbitAccessControlList acl = (JackrabbitAccessControlList) policy;
        Privilege[] jcrAll = AccessControlUtils.privilegesFromNames(acMgr, new String[]{"jcr:all"});
        acl.addEntry(EveryonePrincipal.getInstance(), jcrAll, false);
        acMgr.setPolicy(path, acl);
        break;
      }
    }
  }

}
