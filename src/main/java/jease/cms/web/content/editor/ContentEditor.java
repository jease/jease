/*
    Copyright (C) 2016 maik.jablonski@jease.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jease.cms.web.content.editor;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import jease.Names;
import jease.Registry;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeEditor;
import jease.cms.domain.Content;
import jease.cms.domain.Factory;
import jease.cms.domain.User;
import jease.cms.service.Contents;
import jease.cms.service.Properties;
import jease.cms.service.Revisions;
import jease.cms.service.Sequences;
import jease.cms.web.content.editor.property.PropertyManager;
import jfix.util.I18N;
import jfix.zk.Images;
import jfix.zk.Modal;
import jfix.zk.Selectfield;
import jfix.zk.Sessions;
import jfix.zk.WebBrowser;

/**
 * Base class for all content editors. All common fields for Content should be
 * added here.
 */
public abstract class ContentEditor<E extends Content> extends NodeEditor<E> {

	protected Date lastNodeModification;
	protected Textbox title = new Textbox();
	protected Textbox tags = new Textbox();
	protected Selectfield revisionSelection = new Selectfield();
	protected PropertyManager propertyManager = new PropertyManager();
	protected Button editProperties = new Button(I18N.get("Properties"),
			Images.DocumentProperties);
	protected Button viewContent = new Button(I18N.get("View"),
			Images.InternetWebBrowser);
	protected boolean closeCheckEnabled;
	protected SimpleDateFormat month_date = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);

	public ContentEditor() {
		if (getSessionUser().isAdministrator()) {
			editProperties.addEventListener(Events.ON_CLICK,
					event -> propertyManager.toogleEdit());
			getButtons().appendChild(editProperties);
		}

		if (Registry.getParameter(Names.JEASE_SITE_DESIGN) != null) {
			viewContent.addEventListener(Events.ON_CLICK,
					event -> viewContent());
			getButtons().appendChild(viewContent);
		}

		revisionSelection.addEventListener(
				Events.ON_SELECT,
				event -> {
					peek(Revisions.checkout(getNode(),
							revisionSelection.getSelectedIndex()));
					viewContent.setVisible(true);
					lastNodeModification = null;
				});

		addEventListener(Events.ON_CLOSE, event -> closePerformed(event));
		closeCheckEnabled = true;
	}

	@Override
	public void refresh() {
		super.refresh();
		notifyAboutMaintenance();
	}

	protected void notifyAboutMaintenance() {
		String message = Registry.getParameter(Names.JEASE_CMS_MAINTENANCE);
		if (StringUtils.isNotBlank(message)
				&& !getSessionUser().isAdministrator()) {
			Modal.info(message, event -> Sessions.invalidate());
		}
	}

	@Override
	protected void doInit() throws Exception {
		if (Revisions.isConfigured()) {
			add(I18N.get("Revision"), revisionSelection);
		}
		super.doInit();
		add(I18N.get("Title"), title);
		add(I18N.get("Tags"), tags);
		init();
		add(propertyManager);
	}

	@Override
	protected void doLoad() throws Exception {
		super.doLoad();
		viewContent.setVisible(Nodes.isRooted(getNode()));
		lastNodeModification = getNode().getLastModified();
		if (title.getParent() != null) {
			title.setText(getNode().getTitle());
		}
		if (tags.getParent() != null) {
			tags.setText(getNode().getTages());
		}
		if (revisionSelection.getParent() != null) {
			revisionSelection.setValues(getNode().getRevisions());
		}
		if (propertyManager.getParent() != null) {
			Factory factory = Properties.getFactory(
					(Content) JeaseSession.getContainer(), getNode());
			if (factory != null && factory != JeaseSession.getContainer()) {
				if (StringUtils.isEmpty(id.getValue())
						&& StringUtils.isNotBlank(factory.getSequence())) {
					id.setText(String.valueOf(Sequences.getNext(factory
							.getSequence())));
				}
				propertyManager.setProperties(factory.getProperties(getNode()));
			} else {
				propertyManager.setProperties(getNode().getProperties());
			}
		}
		load();
	}

	protected void saveEditorToObject() throws Exception {
		super.doSave();
		if (getNode().getCreationDate() == null) {
			getNode().setCreationDate(new Date());
		}
		saveLastModification();
		if (title.getParent() != null) {
			getNode().setTitle(title.getText());
		}
		if (tags.getParent() != null) {
			getNode().setTages(tags.getText());
		}
		if (propertyManager.getParent() != null) {
			getNode().setProperties(propertyManager.getProperties());
		}
		save();
	}

	@Override
	protected void doSave() throws Exception {
		saveEditorToObject();
		insertToSolr();
		persist();
	}

	public void insertToSolr() {

		String solrurl = jease.Registry.getParameter(jease.Names.JEASE_SOLR_URL, "");
		if (solrurl.equals("")) return;

		String oid = checkDuplication();
		if (oid.length() > 0) {
			updateToSolr(oid);
			return;
		}
		try {
			ArrayList<String> tagslist = new ArrayList<String>(Arrays.asList(tags.getValue().split(",")));
			SolrClient client = new HttpSolrClient.Builder(solrurl).build();
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", UUID.randomUUID().toString());
			doc.addField("tags", tagslist);
			doc.addField("jeaseid", id.getValue());
			doc.addField("jeasepath", getNode().getPath());
			doc.addField("title", title.getValue());
			doc.addField("author", getNode().getEditor().getName());
			doc.addField("type", getNode().getType());
			doc.addField("text", getNode().getFulltext().toString());
			doc.addField("last_modified", new Date());
			doc.addField("date", month_date.format(new Date()));
			doc.addField("category", getNode().getParent().getId());
			client.add(doc);
			client.commit();
		} catch (Exception s) {
			s.printStackTrace();
		}
	}

	private String checkDuplication() {
		try {
			String solrurl = jease.Registry.getParameter(jease.Names.JEASE_SOLR_URL, "");
			if (solrurl.equals("")) return "";
			SolrClient client = new HttpSolrClient.Builder(solrurl).build();

			SolrQuery query = new SolrQuery();
			query.setQuery("*:*");
			query.setFilterQueries("jeaseid:\"" + id.getValue() + "\" ");
			query.setFilterQueries("jeasepath:\"" + getNode().getPath() + "\"");
			SolrDocumentList results = client.query(query).getResults();
			if (results.size() > 0) {
				return results.get(0).getFieldValue("id").toString();
			}
		} catch (Exception s) {
			s.printStackTrace();
		}
		return "";
	}

	public void deleteToSolr(String docid) {
		String solrurl = jease.Registry.getParameter(jease.Names.JEASE_SOLR_URL, "");
		if (solrurl.equals("")) return;

		HttpSolrClient solr = new HttpSolrClient.Builder(solrurl).build();
		try {
			solr.deleteById(docid);
			solr.commit();
		}catch(Exception d){
			d.printStackTrace();
		}
	}
	public void updateToSolr(String docid) {
		String solrurl = jease.Registry.getParameter(jease.Names.JEASE_SOLR_URL, "");
		if (solrurl.equals("")) return;

		HttpSolrClient solr = new HttpSolrClient.Builder(solrurl).build();
		try {
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id",docid);
			Map<String, Object> fieldModifier = new HashMap<>(1);
			fieldModifier.put("set", this.getNode().getFulltext());
			document.addField("text", fieldModifier);

			Map<String, Object> fieldModifier1 = new HashMap<>(1);
			fieldModifier1.put("set", this.getNode().getTitle());
			document.addField("title", fieldModifier1);

			Map<String, Object> fieldModifier2 = new HashMap<>(1);
			fieldModifier2.put("set", this.getNode().getTages());
			document.addField("tags", fieldModifier2);

			Map<String, Object> fieldModifier3 = new HashMap<>(1);
			fieldModifier3.put("set", new Date());
			document.addField("last_modified", fieldModifier3);

			Map<String, Object> fieldModifier4 = new HashMap<>(1);
			fieldModifier4.put("set", month_date.format(new Date()));
			document.addField("date", fieldModifier4);

			Map<String, Object> fieldModifier5 = new HashMap<>(1);
			fieldModifier5.put("set", id.getValue());
			document.addField("jeaseid", fieldModifier5);



			solr.add(document);
			solr.commit();
		} catch (Exception s) {
			s.printStackTrace();
		}
	}

	protected void saveLastModification() {
		User lastEditor = getNode().getEditor();
		User currentUser = getSessionUser();
		if (lastEditor == null || lastEditor.isAdministrator()
				|| !currentUser.isAdministrator()) {
			getNode().setEditor(currentUser);
			getNode().setLastModified(new Date());
		}
	}

	protected void persist() {
		if (Revisions.isConfigured()) {
			Revisions.checkin(getSessionUser().getLogin(), getNode());
		}
		Nodes.save(Contents.customize(getNode()));
	}

	@Override
	protected void doValidate() throws Exception {
		if (StringUtils.isEmpty(id.getValue()) && id.isVisible()
				&& !id.isDisabled()) {
			id.setValue(title.getValue());
		}
		super.doValidate();
		if (!isFactoryMode()) {
			if (title.getParent() != null) {
				validate(StringUtils.isEmpty(title.getValue()),
						I18N.get("Title_is_required"));
			}
			validate(lastNodeModification != null
							&& lastNodeModification != getNode().getLastModified(),
					I18N.get("Content_is_stale"));
			validate();
		}
	}

	@Override
	public void doCopy() throws Exception {
		super.doCopy();
		if (revisionSelection.getParent() != null) {
			revisionSelection.setValues(new Object[]{});
		}
		viewContent.setVisible(false);
	}

	@Override
	public void delete() {
		closeCheckEnabled = false;
		getNode().setEditor(getSessionUser());
		getNode().setLastModified(new Date());
		deleteToSolr(checkDuplication());
		Contents.delete(getNode());
	}

	protected User getSessionUser() {
		return JeaseSession.get(User.class);
	}

	@Override
	public void hideButtons() {
		super.hideButtons();
		viewContent.setVisible(false);
		editProperties.setVisible(false);
	}

	protected boolean isFactoryMode() {
		return JeaseSession.getContainer() instanceof Factory;
	}

	protected void closePerformed(final Event event) {
		if (closeCheckEnabled) {
			final E currentNode = getNode();
			String currentFulltext = getNode().getFulltext().toString();
			try {
				copyObject(false);
				saveEditorToObject();
				getNode().setParent(null);
				String newFulltext = getNode().getFulltext().toString();
				if (!StringUtils.equals(currentFulltext, newFulltext)) {
					event.stopPropagation();
					Modal.confirm(I18N.get("Are_you_sure"), evt -> {
						closeCheckEnabled = false;
						refresh();
						fireClose();
					});
				}
			} catch (Exception e) {
				Modal.exception(e);
				return;
			} finally {
				setObject(currentNode);
			}
		}
	}

	protected void viewContent() {
		final E currentNode = getNode();
		try {
			copyObject(false);
			saveEditorToObject();
			getNode().setParent(null);
			Sessions.set(currentNode.getPath(), getNode());
		} catch (Exception e) {
			Modal.exception(e);
			return;
		} finally {
			setObject(currentNode);
		}
		WebBrowser browser = new WebBrowser(getNode().getPath());
		browser.addEventListener(Events.ON_CLOSE,
				event -> Sessions.remove(currentNode.getPath()));
		getRoot().appendChild(browser);
	}

}
