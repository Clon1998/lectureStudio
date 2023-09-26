/*
 * Copyright (C) 2020 TU Darmstadt, Department of Computer Science,
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

package org.lecturestudio.presenter.swing.view;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.lecturestudio.core.view.Action;
import org.lecturestudio.presenter.api.view.PreviewStreamView;
import org.lecturestudio.swing.util.SwingUtils;
import org.lecturestudio.swing.view.SwingView;

@SwingView(name = "stream-preview")
public class SwingPreviewStreamView extends JPanel implements PreviewStreamView {

	private JPanel contentContainer;

	private Component browserContent;

	private JButton closeButton;


	SwingPreviewStreamView() {
		super();
	}

	@Override
	public void setBrowserComponent(Component component) {
		SwingUtils.invoke(() -> {
			contentContainer.add(component);
		});
	}

	@Override
	public void setOnClose(Action action) {
		SwingUtils.bindAction(closeButton, action);
	}
}
