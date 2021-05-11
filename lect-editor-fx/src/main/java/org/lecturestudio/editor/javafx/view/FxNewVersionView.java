/*
 * Copyright (C) 2021 TU Darmstadt, Department of Computer Science,
 * Embedded Systems and Applications Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lecturestudio.editor.javafx.view;

import java.util.ResourceBundle;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.NewVersionView;
import org.lecturestudio.javafx.control.NotificationPane;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxmlView;

@FxmlView(name = "new-version")
public class FxNewVersionView extends NotificationPane implements NewVersionView {

	@FXML
	private Button closeButton;

	@FXML
	private Button downloadButton;

	@FXML
	private Button openUrlButton;


	@Inject
	public FxNewVersionView(ResourceBundle resources) {
		super(resources);
	}

	@Override
	public void setOnClose(Action action) {
		FxUtils.bindAction(closeButton, action);
	}

	@Override
	public void setOnDownload(Action action) {
		FxUtils.bindAction(downloadButton, action);
	}

	@Override
	public void setOnOpenUrl(Action action) {
		FxUtils.bindAction(openUrlButton, action);
	}
}
