# README #

# **Git commands** #
* To save your work and upload/push to the repository
  git pull (to update your branches with the repository otherwise git will complain)
  git add -A
  git commit -m "message of what you have done"  (e.g. git commit -m "Created class to save the data" )
  git push origin name-of-your-branch            (e.g. git push origin backend-saveData) - for the saveData 

* To change branches
  git checkout name-of-the-branch

* To create a branch
  navigate to the branch where you want to create a new branch from using
  git checkout name-of-the-branch                (e.g. git checkout backend-loadData)
  git checkout -b name-for-new-branch            (e.g. git checkout -b gui-map)
  git push origin name-for-new-branch            (e.g. git push origin gui-map)


# **Pull Requests** #
When your work is ready 
* Click on the branches icon on the bitbucket webpage for the repository
* At the end of the branch that is ready click the three dots
* Select 'Create Pull Request'
* Type in [Ready] followed by a short title description
* Click the create button