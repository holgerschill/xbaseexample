/**
 * generated by Xtext
 */
package org.eclipse.eXXXtreme.validation;

import com.google.inject.Inject;
import java.io.File;
import java.util.Set;
import org.eclipse.eXXXtreme.h2.H2MetaDataAccess;
import org.eclipse.eXXXtreme.tutorial.Model;
import org.eclipse.eXXXtreme.tutorial.Query;
import org.eclipse.eXXXtreme.tutorial.TutorialPackage;
import org.eclipse.eXXXtreme.validation.AbstractTutorialValidator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;

/**
 * This class contains custom validation rules.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
@SuppressWarnings("all")
public class TutorialValidator extends AbstractTutorialValidator {
  @Inject
  @Extension
  private H2MetaDataAccess _h2MetaDataAccess;
  
  @Check
  public void checkIfH2FileExists(final Model model) {
    final String h2Path = model.getH2Path();
    boolean _startsWith = h2Path.startsWith("/");
    if (_startsWith) {
      this.error("Path should be relativ to project like \'db/test\'.", TutorialPackage.Literals.MODEL__H2_PATH);
    }
    String _projectPath = this._h2MetaDataAccess.getProjectPath(model);
    String _plus = (_projectPath + "/");
    String _plus_1 = (_plus + h2Path);
    String _plus_2 = (_plus_1 + ".mv.db");
    final File file = new File(_plus_2);
    boolean _exists = file.exists();
    boolean _not = (!_exists);
    if (_not) {
      this.error("File does not exist!", TutorialPackage.Literals.MODEL__H2_PATH);
    }
  }
  
  @Check
  public void checkUniqueQueryNames(final Model model) {
    final Set<String> names = CollectionLiterals.<String>newHashSet();
    EList<Query> _queries = model.getQueries();
    for (final Query query : _queries) {
      String _name = query.getName();
      boolean _add = names.add(_name);
      boolean _not = (!_add);
      if (_not) {
        this.error("Name of query is not unique!", query, TutorialPackage.Literals.QUERY__NAME);
      }
    }
  }
}