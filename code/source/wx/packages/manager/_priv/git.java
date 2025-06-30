package wx.packages.manager._priv;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.attributes.AttributesNodeProvider;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.lib.BaseRepositoryBuilder;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.ReflogReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.TransportHttp;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.util.FS;
import com.wm.util.JournalLogger;
// --- <<IS-END-IMPORTS>> ---

public final class git

{
	// ---( internal utility methods )---

	final static git _instance = new git();

	static git _newInstance() { return new git(); }

	static git _cast(Object o) { return (git)o; }

	// ---( server methods )---




	public static final void cloneGitRepo (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(cloneGitRepo)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required uri
		// [i] field:0:optional repoName
		// [i] field:0:optional tag
		// [i] field:0:optional user
		// [i] field:0:optional password
		// [i] field:0:required localDir
		// [i] field:0:optional privateKeyFile
		// [i] field:0:optional passPhrase
		// [i] record:0:optional auth
		// [i] - field:0:optional type
		// [i] - field:0:optional user
		// [i] - field:0:optional pass
		// [i] - field:0:optional delegation {"none","kerberos"}
		// [i] - field:0:optional token
		// [i] - record:0:optional kerberos
		// [i] -- field:0:optional jaasContext
		// [i] -- field:0:optional clientPrincipal
		// [i] -- field:0:optional clientPassword
		// [i] -- field:0:optional servicePrincipal
		// [i] -- field:0:optional servicePrincipalForm
		// [i] -- field:0:optional requestDelegatableToken {"true","false"}
		// pipeline in
		
		IDataCursor cursor = pipeline.getCursor();
		String uri = IDataUtil.getString(cursor, "uri");
		String repo = IDataUtil.getString(cursor, "repoName");
		String tag = IDataUtil.getString(cursor, "tag");
		String user = IDataUtil.getString(cursor, "user");
		String password = IDataUtil.getString(cursor, "password");		
		String localDirStr = IDataUtil.getString(cursor, "localDir");
		String pathToPrivateKey = IDataUtil.getString(cursor, "privateKeyFile");
		String passPhrase = IDataUtil.getString(cursor, "passPhrase");
		IData inDoc = IDataUtil.getIData(cursor, "auth" );
		
		// process
		
		if (repo != null) {
			if (uri.endsWith("/"))
				uri += repo + ".git";
			else
				uri += "/" + repo + ".git";
		}
		
		File localDir = new File(localDirStr);
		
		if (!localDir.exists()) {
			localDir.mkdirs();
		} else if (!localDir.isDirectory()) {
			throw new ServiceException("BuildDir must be a directory: " + localDirStr);
		} else {
			
			System.out.println("Delete existing directory " + localDirStr);
		
			deleteDir(localDir);
			
			localDir.mkdir();
		}
				
		System.out.println("Cloning from " + uri);
		
		CloneCommand c = new CloneCommand();
		
		if (pathToPrivateKey != null) {
					 
			 if (uri.startsWith("http")) {
		
		
				// input
				IData input = IDataFactory.create();
				IDataCursor inputCursor = input.getCursor();
				IDataUtil.put( inputCursor, "url", uri );
				inputCursor.destroy();
		
				// output
				IData 	output = IDataFactory.create();
				try{
					output = Service.doInvoke( "wx.packages.manager._priv.tools", "splitUrl", input );
				}catch( Exception e){}
				IDataCursor outputCursor = output.getCursor();
				String	owner = IDataUtil.getString( outputCursor, "owner" );
				repo = IDataUtil.getString( outputCursor, "repo" );
				String	repoHost = IDataUtil.getString( outputCursor, "repoHost" );
				outputCursor.destroy();
		
			     
			     uri = "git@" + repoHost + ":" + owner + "/" + repo + ".git";
			     
				 c.setURI(uri);
		
				System.out.println("Nope, now cloning from " + uri);
				
				JournalLogger.log(4,90,3,"[WPM]", "Selected Github clone URI: " + uri);
		
			 } else {
				c.setURI(uri);
			 }
			 
			 //c.setTransportConfigCallback(sshCallback(pathToPrivateKey));
			 
			 c.setTransportConfigCallback(
					 transport -> {
					   if(transport instanceof SshTransport) {
					     SshTransport sshTransport = (SshTransport) transport;
					     // Set passphrase using a CustomCredentialsProvider
					     sshTransport.setCredentialsProvider(new CustomCredentialProvider(passPhrase));
					     // Provide private key to `CustomSshSessionFactory`
					     SshSessionFactory sshFactory = new CustomSshSessionFactory(pathToPrivateKey);
					     sshTransport.setSshSessionFactory(sshFactory);
		
					   }
			});
		
		} else if (inDoc != null) {
			IDataCursor inDocCursor = inDoc.getCursor();
			String authType = IDataUtil.getString( inDocCursor, "type" );			
			inDocCursor.destroy();
			
			CredentialsProvider credsProvider = null;
			
			if ("bearer".equalsIgnoreCase(authType)) {
				String token = IDataUtil.getString(inDocCursor, "token");				
				credsProvider = new UsernamePasswordCredentialsProvider(token, token);
				JournalLogger.log(4,90,3,"[WPM]", String.format("Selected Github App authentication: %s %s", uri, "*"));
				
			} else {
				user = IDataUtil.getString(inDocCursor, "user");
				password = IDataUtil.getString(inDocCursor, "pass");
				if (user != null && !user.equals("null")) {
					credsProvider = new UsernamePasswordCredentialsProvider(user, password);
					JournalLogger.log(4,90,3,"[WPM]", String.format("Selected Github creds: %s %s %s", uri, user, "*"));
		
				}				
			}
			c.setCredentialsProvider(credsProvider);
			c.setURI(uri);
		} else {
			c.setURI(uri);
		}
		
		//c.setCloneSubmodules(true);
		
		if (tag != null) {
			c.setBranch(tag);
		}
		
		c.setDirectory(localDir);
		
		try {
			c.call();
						
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
			throw new ServiceException("Invalid GIT source: " + e.getMessage());
		} catch (TransportException e) {
			e.printStackTrace();
			throw new ServiceException("Got an exception connecting to GIT repo: " + e.getMessage());
		} catch (JGitInternalException e) {
			e.printStackTrace();
			throw new ServiceException("Got an internal exception from GIT API: This might be caused by a badly referenced submodule, " + e.getMessage());
		} catch (GitAPIException e) {
			e.printStackTrace();
			throw new ServiceException("Got an exception from GIT API: " + e.getMessage());
		}
		
		// pipeline out
		
		cursor.destroy();
			
			
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void tags (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(tags)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required gitUrl
		// [i] field:0:optional gitUser
		// [i] field:0:optional gitPassword
		// [i] field:0:optional tag
		// [i] record:0:optional auth
		// [i] - field:0:optional type
		// [i] - field:0:optional user
		// [i] - field:0:optional pass
		// [i] - field:0:optional delegation {"none","kerberos"}
		// [i] - field:0:optional token
		// [i] - record:0:optional kerberos
		// [i] -- field:0:optional jaasContext
		// [i] -- field:0:optional clientPrincipal
		// [i] -- field:0:optional clientPassword
		// [i] -- field:0:optional servicePrincipal
		// [i] -- field:0:optional servicePrincipalForm
		// [i] -- field:0:optional requestDelegatableToken {"true","false"}
		// [o] field:1:required tags
		IDataCursor c = pipeline.getCursor();
		CredentialsProvider credsProvider = null;
		// inDoc
		IData inDoc = IDataUtil.getIData( c, "auth" );
		String url = IDataUtil.getString(c, "gitUrl");
		String tag = IDataUtil.getString(c, "tag"); 
		if ( inDoc != null) {
			IDataCursor inDocCursor = inDoc.getCursor();
			String authType = IDataUtil.getString( inDocCursor, "type" );			
			inDocCursor.destroy();
			
			if ("bearer".equalsIgnoreCase(authType)) {
				String token = IDataUtil.getString(inDocCursor, "token");				
				credsProvider = new UsernamePasswordCredentialsProvider(token, token);
				JournalLogger.log(4,90,3,"[WPM]", String.format("Selected Github App authentication: %s %s", url, "*"));
				
			} else {
				String user = IDataUtil.getString(inDocCursor, "user");
				String pass = IDataUtil.getString(inDocCursor, "pass");
				if (user != null && !user.equals("null")) {
					credsProvider = new UsernamePasswordCredentialsProvider(user, pass);
					JournalLogger.log(4,90,3,"[WPM]", String.format("Selected Github creds: %s %s %s", url, user, "*"));
		
				}				
			}
		}
		c.destroy();			
		
				
		// process		
		ArrayList<String> tags = new ArrayList<String>();
		
		try {
			Collection<Ref> map = Git.lsRemoteRepository()
		        .setRemote(url)
		        .setTags(true)
		        .setCredentialsProvider(credsProvider)
		        .call();
		
		    
			for (Ref entry : map) {				
				
				ObjectId peeledRef = entry.getPeeledObjectId();
				
				if (tag != null) {
										
					if (entry.getName().substring(10).equals(tag)) {
						if (peeledRef != null) {
							tags.add(peeledRef.getName());
						} else {
							tags.add(entry.getObjectId().getName());
						}
					}
				} else {
					tags.add(entry.getName().substring(10));
				}
		    }
		} catch (GitAPIException e) {
			throw new ServiceException(e);
		}
			
		// pipeline out
		
		IDataUtil.put(c, "tags", tags.toArray(new String[tags.size()]));
			
			
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
		
	public static final class CustomSshSessionFactory extends SshdSessionFactory {
	
		
	    private final Path privateKeyFile;
	
	    public CustomSshSessionFactory(String privateKeyFile) {
	        this.privateKeyFile = Path.of(privateKeyFile);
	    }
	
	    @Override
	    public File getSshDirectory() {
	        
	    	return privateKeyFile.getParent().toFile();
	    }
	
	// Return paths to private key files
	    @Override
	    protected List<Path> getDefaultIdentities(File sshDir) {
	        
	    	return Collections.singletonList(privateKeyFile);
	    }
	
	}
	
	public static class CustomCredentialProvider extends CredentialsProvider {
		
	    private final String passphrase;
	
	    public CustomCredentialProvider(String passphrase) {
	        this.passphrase = passphrase;
	    }
	
	    @Override
	    public boolean isInteractive() {
	        // Set this according to your requirement
	        return false;
	    }
	
	    @Override
	    public boolean supports(CredentialItem... items) {
	        // Set this according to your requirement
	        return true;
	    }
	
	    @Override
	    public boolean get(URIish uri, CredentialItem... items)
	            throws UnsupportedCredentialItem {
	
	        for (CredentialItem item : items) {
	            if (item instanceof CredentialItem.InformationalMessage) {
	                continue;
	            }
	            if (item instanceof CredentialItem.YesNoType) {
	                // Set this according to your requirement
	                ((CredentialItem.YesNoType) item).setValue(true);
	            } else if (item instanceof CredentialItem.CharArrayType) {
	                if (passphrase != null) {
	                    ((CredentialItem.CharArrayType) item)
	                            .setValue(passphrase.toCharArray());
	                } else {
	                    return false;
	                }
	            } else if (item instanceof CredentialItem.StringType) {
	                if (passphrase != null) {
	                    ((CredentialItem.StringType) item)
	                            .setValue(passphrase);
	                } else {
	                    return false;
	                }
	            } else {
	                return false;
	            }
	        }
	        return true;
	    }
	}
	
		/*private static TransportConfigCallback sshCallback(String pathToPrivateKey) {
		
			return new TransportConfigCallback() {
	           
				@Override
				public void configure(Transport transport) {
					SshTransport sshTransport = (SshTransport) transport;
	                sshTransport.setSshSessionFactory(getSSHConfig(pathToPrivateKey));
					
				}
	        };
		}
		
		private static JschConfigSessionFactory getSSHConfig(String pathToPrivateKey) {
			
			return new JschConfigSessionFactory() {
				
				@Override
				protected void configure(Host hc, Session session) {
			        session.setConfig("StrictHostKeyChecking", "yes");
					super.configure(hc, session);
				}
				
				@Override
				protected JSch getJSch(Host hc, FS fs) throws JSchException {
					JSch ch = super.getJSch(hc, fs);
			        ch.addIdentity(pathToPrivateKey);
	
					return ch;
				}
			};
		}*/
		
		private static void deleteDir(File file) {
		    File[] contents = file.listFiles();
		    if (contents != null) {
		        for (File f : contents) {
		            if (! Files.isSymbolicLink(f.toPath())) {
		                deleteDir(f);
		            }
		        }
		    }
		    file.delete();
		}
		
		
		
		
		
		
	// --- <<IS-END-SHARED>> ---
}

