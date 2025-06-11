package com.iyuba.talkshow.injection.component;

import com.iyuba.talkshow.injection.PerDialog;
import com.iyuba.talkshow.ui.dubbing.dialog.DubbingDialog;
import com.iyuba.talkshow.ui.dubbing.dialog.download.DownloadDialog;
import com.iyuba.talkshow.ui.user.edit.dialog.SchoolDialog;

import dagger.Subcomponent;

@PerDialog
@Subcomponent
public interface DialogComponent {
        void inject(DubbingDialog dubbingDialog);

        void inject(SchoolDialog schoolDialog);

        void inject(DownloadDialog downloadDialog);
}