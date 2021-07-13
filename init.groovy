import jenkins.*
import hudson.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.plugins.sshslaves.*;
import hudson.model.*
import jenkins.model.*
import hudson.security.*

global_domain = Domain.global()
credentials_store =
  Jenkins.instance.getExtensionList(
    'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
  )[0].getStore()

credentials = new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL,null,"root",new BasicSSHUserPrivateKey.UsersPrivateKeySource(),"","")

credentials_store.addCredentials(global_domain, credentials)

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
def adminUsername = System.getenv('JENKINS_ADMIN_USERNAME') ?: 'admin'
def adminPassword = System.getenv('JENKINS_ADMIN_PASSWORD') ?: 'YOUR_PASSWORD'
hudsonRealm.createAccount(adminUsername, adminPassword)
hudsonRealm.createAccount("your_user", "YOUR_PASSWORD")

def instance = Jenkins.getInstance()
instance.setSecurityRealm(hudsonRealm)
instance.setInstallState(InstallState.INITIAL_SETUP_COMPLETED)
instance.save()


def strategy = new GlobalMatrixAuthorizationStrategy()

//  Slave Permissions
strategy.add(hudson.model.Computer.BUILD,'your_user')
strategy.add(hudson.model.Computer.CONFIGURE,'your_user')
strategy.add(hudson.model.Computer.CONNECT,'your_user')
strategy.add(hudson.model.Computer.CREATE,'your_user')
strategy.add(hudson.model.Computer.DELETE,'your_user')
strategy.add(hudson.model.Computer.DISCONNECT,'your_user')

//  Credential Permissions
strategy.add(com.cloudbees.plugins.credentials.CredentialsProvider.CREATE,'your_user')
strategy.add(com.cloudbees.plugins.credentials.CredentialsProvider.DELETE,'your_user')
strategy.add(com.cloudbees.plugins.credentials.CredentialsProvider.MANAGE_DOMAINS,'your_user')
strategy.add(com.cloudbees.plugins.credentials.CredentialsProvider.UPDATE,'your_user')
strategy.add(com.cloudbees.plugins.credentials.CredentialsProvider.VIEW,'your_user')

//  Overall Permissions
strategy.add(hudson.model.Hudson.ADMINISTER,'your_user')
strategy.add(hudson.PluginManager.CONFIGURE_UPDATECENTER,'your_user')
strategy.add(hudson.model.Hudson.READ,'your_user')
strategy.add(hudson.model.Hudson.RUN_SCRIPTS,'your_user')
strategy.add(hudson.PluginManager.UPLOAD_PLUGINS,'your_user')

//  Job Permissions
strategy.add(hudson.model.Item.BUILD,'your_user')
strategy.add(hudson.model.Item.CANCEL,'your_user')
strategy.add(hudson.model.Item.CONFIGURE,'your_user')
strategy.add(hudson.model.Item.CREATE,'your_user')
strategy.add(hudson.model.Item.DELETE,'your_user')
strategy.add(hudson.model.Item.DISCOVER,'your_user')
strategy.add(hudson.model.Item.READ,'your_user')
strategy.add(hudson.model.Item.WORKSPACE,'your_user')

//  Run Permissions
strategy.add(hudson.model.Run.DELETE,'your_user')
strategy.add(hudson.model.Run.UPDATE,'your_user')

//  View Permissions
strategy.add(hudson.model.View.CONFIGURE,'your_user')
strategy.add(hudson.model.View.CREATE,'your_user')
strategy.add(hudson.model.View.DELETE,'your_user')
strategy.add(hudson.model.View.READ,'your_user')

//  Setting Anonymous Permissions
//strategy.add(hudson.model.Hudson.READ,'anonymous')
//strategy.add(hudson.model.Item.BUILD,'anonymous')
//strategy.add(hudson.model.Item.CANCEL,'anonymous')
//strategy.add(hudson.model.Item.DISCOVER,'anonymous')
//strategy.add(hudson.model.Item.READ,'anonymous')

// Setting Admin Permissions
strategy.add(Jenkins.ADMINISTER, "admin")

// Setting easy settings for local builds
def local = System.getenv("BUILD").toString()
if(local == "local") {
  //  Overall Permissions
  //strategy.add(hudson.model.Hudson.ADMINISTER,'anonymous')
  //strategy.add(hudson.PluginManager.CONFIGURE_UPDATECENTER,'anonymous')
  //strategy.add(hudson.model.Hudson.READ,'anonymous')
  //strategy.add(hudson.model.Hudson.RUN_SCRIPTS,'anonymous')
  //strategy.add(hudson.PluginManager.UPLOAD_PLUGINS,'anonymous')
}

instance.setAuthorizationStrategy(strategy)
instance.save()
