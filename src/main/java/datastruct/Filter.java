package datastruct;

import datastruct.feature.IFeature;
import datastruct.feature.Location;

/**
 * Filter represents for set of user's preference in terms of some specific features.
 */
public class Filter {
    private IFeature[] filters;

    public IFeature[] getFilters() {
        return filters;
    }

    public IFeature getFilter(IFeature.FeatureType type) {
        if (filters != null) {
            for (IFeature f : filters) {
                if (f.getType().equals(type))
                    return f;
            }
        }

        return null;
    }
}
