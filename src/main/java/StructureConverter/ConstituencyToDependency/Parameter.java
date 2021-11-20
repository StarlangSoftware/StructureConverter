package StructureConverter.ConstituencyToDependency;

import Classification.Model.TreeEnsembleModel;

import java.util.ArrayList;

public class Parameter {

    private boolean model = false;
    private final ArrayList<TreeEnsembleModel> models;
    private final Language language;

    public Parameter(ArrayList<TreeEnsembleModel> models, Language language) {
        if (models != null) {
            this.model = true;
        }
        this.models = models;
        this.language = language;
    }

    public boolean getModel() {
        return this.model;
    }

    public ArrayList<TreeEnsembleModel> getModels() {
        return this.models;
    }

    public Language getLanguage() {
        return this.language;
    }
}
