package SongCreatorWindow.Model.Core;

import Images.ImageManager;

public class GlobalLoaderDTO
{
    boolean loadProject;
    String projectLocation;

    static GlobalLoaderDTO instance;
    private GlobalLoaderDTO(){  }
    public static GlobalLoaderDTO getInstance()
    {
        if(instance == null)
            synchronized (GlobalLoaderDTO.class) {
                if(instance == null)
                    instance = new GlobalLoaderDTO();
            }
        return instance;
    }

    public void setLoadingData(String pathToProject)
    {
        synchronized (GlobalLoaderDTO.class)
        {
            projectLocation = pathToProject;
            loadProject = true;
        }
    }

    public boolean isHereProjectToLoad()
    {
        return loadProject;
    }

    public String getProjectDestinationOnce()
    {
        synchronized (GlobalLoaderDTO.class)
        {
            loadProject = false;
            return projectLocation;
        }
    }
}
