# W.I.P - Usage guide
Reading this isn't going to be easy, but I recommend you try to read it and then go to the [examples]() afterward.<br>
If you manage to do it you can give yourself a pat on the back, I have no cookies left.

## Content Table
* [Instantiating the AddonLoader](#instantiating-the-addonloader)
* [Addon's info file](#addon-s-info-file)
* [Initializing the AddonLoader]()
* [Creating a Reflection Task]()
* [Creating a Callback Task]()
* [Declaring an Addon's Class]()
* ["Passing" data with the AddonEvent]()
* [Executing the Tasks / Loading Stuff]()

## Instantiating the AddonLoader
**Note:** The constructors with the boolean aren't implemented yet, use the (String[], String) one.

You can instanciate the AddonLoader in 4 different ways:

```java
// These 3 constructors will call the AddonLoader(String[], String, boolean) one
// with those values for the missing parameters:
// * addonsFolder      => "./addons/"
// * checkDependencies => false
AddonLoader al = new AddonLoader(new String[] { "test" });

AddonLoader al = new AddonLoader(new String[] { "test" }, "./addons/");

AddonLoader al = new AddonLoader(new String[] { "test" }, true);

// This constructor will be called by all the others.
AddonLoader al = new AddonLoader(new String[] { "test" }, "./addons/", true);
```
The arguments for the constructor are [as follow] [in this order]:
* **addonsIds** - Array that contains all the [addons' IDs] - See the [Addon's info file]() section.
* **addonsFolder** - The addons folder's path.
* **checkDependencies** - Check for addons dependencies [switch].

**Example:** [ConstructorExample.java](src/example/java/com/azias/module/addons/examples/ConstructorExample.java)

## Addon's info file
W.I.P section

This file contains various informations about an addon.

| Fields | Type | Usage |
| --- | --- | --- |
| id | String | Will be used to find and execute the addon's class<br>Might also be used to load ressources from the addons folder depending on the implementation. |
| name | String | [Friendly name] |
| description | String | The addon's description |
| authors | String[] | The authors' names |
| credits | String[] | ... |
| version | [Version]() | ... |
| versionUrl | String | The url of a file/page where the current addon's version can be found. |
| projectUrl | String | The project's page url. |
| updateUrl | String | ... |
| dependencies | HashMap<br>\<String, [Version]()\> | NOT IMPLEMENTED/USED YET |
**Example:** [addon.json](addons/test/addon.json)

## Initializing the AddonLoader


## Creating a Reflection Task
See [AddonClassCallingTest.java](src/test/java/com/azias/module/addons/AddonClassCallingTest.java)

## Creating a Callback Task


## Declaring an Addon's Class
If you want to 

## "Passing" data with the AddonEvent
See [EventSharedValuesTest.java](src/test/java/com/azias/module/addons/EventSharedValuesTest.java)

## Executing the Tasks / Loading Stuff


## ???

