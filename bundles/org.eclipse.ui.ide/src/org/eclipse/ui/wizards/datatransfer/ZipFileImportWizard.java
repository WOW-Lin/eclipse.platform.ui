/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.wizards.datatransfer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.internal.wizards.datatransfer.WizardZipFileResourceImportPage1;

/**
 * Standard workbench wizard for importing resources from a zip file
 * into the workspace.
 * <p>
 * This class may be instantiated and used without further configuration;
 * this class is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * <pre>
 * IWizard wizard = new ZipFileImportWizard();
 * wizard.init(workbench, selection);
 * WizardDialog dialog = new WizardDialog(shell, wizard);
 * dialog.open();
 * </pre>
 * During the call to <code>open</code>, the wizard dialog is presented to the
 * user. When the user hits Finish, the user-selected zip file is imported
 * into the workspace, the dialog closes, and the call to <code>open</code>
 * returns.
 * </p>
 */
public class ZipFileImportWizard extends Wizard implements IImportWizard {
    private IWorkbench workbench;

    private IStructuredSelection selection;

    private WizardZipFileResourceImportPage1 mainPage;

    /**
     * Creates a wizard for importing resources into the workspace from
     * a zip file.
     */
    public ZipFileImportWizard() {
        AbstractUIPlugin plugin = (AbstractUIPlugin) Platform
                .getPlugin(PlatformUI.PLUGIN_ID);
        IDialogSettings workbenchSettings = plugin.getDialogSettings();
        IDialogSettings section = workbenchSettings
                .getSection("ZipFileImportWizard");//$NON-NLS-1$
        if (section == null)
            section = workbenchSettings.addNewSection("ZipFileImportWizard");//$NON-NLS-1$
        setDialogSettings(section);
    }

    /* (non-Javadoc)
     * Method declared on IWizard.
     */
    public void addPages() {
        super.addPages();
        mainPage = new WizardZipFileResourceImportPage1(workbench, selection);
        addPage(mainPage);
    }

    /**
     * Returns the image descriptor with the given relative path.
     */
    private ImageDescriptor getImageDescriptor(String relativePath) {
        String iconPath = "icons/full/";//$NON-NLS-1$	
        try {
            AbstractUIPlugin plugin = (AbstractUIPlugin) Platform
                    .getPlugin(PlatformUI.PLUGIN_ID);
            URL installURL = plugin.getDescriptor().getInstallURL();
            URL url = new URL(installURL, iconPath + relativePath);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            // Should not happen
            return null;
        }
    }

    /* (non-Javadoc)
     * Method declared on IWorkbenchWizard.
     */
    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        this.workbench = workbench;
        this.selection = currentSelection;
        List selectedResources = IDE.computeSelectedResources(currentSelection);
        if (!selectedResources.isEmpty()) {
            this.selection = new StructuredSelection(selectedResources);
        }

        setWindowTitle(DataTransferMessages
                .getString("DataTransfer.importTitle")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(getImageDescriptor("wizban/importzip_wiz.gif"));//$NON-NLS-1$
        setNeedsProgressMonitor(true);
    }

    /* (non-Javadoc)
     * Method declared on IWizard.
     */
    public boolean performCancel() {
        return mainPage.cancel();
    }

    /* (non-Javadoc)
     * Method declared on IWizard.
     */
    public boolean performFinish() {
        return mainPage.finish();
    }
}