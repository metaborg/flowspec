package mb.flowspec.comlan18;

import org.metaborg.core.editor.IEditorRegistry;
import org.metaborg.core.editor.NullEditorRegistry;
import org.metaborg.core.project.IProjectService;
import org.metaborg.core.project.SingleFileProjectService;
import org.metaborg.spoofax.core.SpoofaxModule;

import com.google.inject.Singleton;

public class SpoofaxModuleExtension extends SpoofaxModule {
    @Override
    protected void bindEditor() {
        bind(IEditorRegistry.class)
            .to(NullEditorRegistry.class)
            .in(Singleton.class);
    }

    @Override
    protected void bindProject() {
        bind(IProjectService.class)
            .to(SingleFileProjectService.class)
            .in(Singleton.class);
    }
}