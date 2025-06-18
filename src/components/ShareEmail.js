import { useState } from 'react';
import { Mail, User, Phone, Paperclip, X, Plus, Lock, Send } from 'lucide-react';
import api from '../services/api';

export default function ShareEmail() {
  const [recipientCount, setRecipientCount] = useState(1);
  const [recipients, setRecipients] = useState([{ email: '', phone: '' }]);
  const [subject, setSubject] = useState('');
  const [body, setBody] = useState('');
  const [file, setFile] = useState(null);
  const [encryptedFile, setEncryptedFile] = useState(null);
  const [password, setPassword] = useState('');
  const [userEmail, setUserEmail] = useState('user@example.com');
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isEncrypting, setIsEncrypting] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const [error, setError] = useState('');

  // Add recipient field (up to 4)
  const addRecipient = () => {
    if (recipients.length < 4) {
      setRecipients([...recipients, { email: '', phone: '' }]);
      setRecipientCount(recipientCount + 1);
    }
  };

  // Remove recipient field
  const removeRecipient = (index) => {
    if (recipients.length > 1) {
      const newRecipients = [...recipients];
      newRecipients.splice(index, 1);
      setRecipients(newRecipients);
      setRecipientCount(recipientCount - 1);
    }
  };

  // Update recipient fields
  const updateRecipient = (index, field, value) => {
    const newRecipients = [...recipients];
    newRecipients[index][field] = value;
    setRecipients(newRecipients);
  };

  // Handle file selection
  const handleFileChange = (e) => {
    if (e.target.files[0]) {
      setFile(e.target.files[0]);
      setEncryptedFile(null); // Reset encrypted file state when new file is selected
    }
  };

  // Handle encryption password dialog open
  const handleEncryptDialog = () => {
    if (file) {
      setIsDialogOpen(true);
    } else {
      setError('Please select a file first');
    }
  };

  // Handle encryption process
  const handleEncryptFile = async () => {
    if (!file || !password) {
      setError('Please select a file and enter a password');
      return;
    }

    setIsEncrypting(true);
    setError('');

    try {
      // Create form data
      const formData = new FormData();
      formData.append('file', file);
      formData.append('password', password);

      // Call API to encrypt file
      const response = await api.encryptFile(formData);
      
      // Set the encrypted file
      setEncryptedFile(response.data);
      setIsDialogOpen(false);
      
      // Success message
      alert('File encrypted successfully!');
    } catch (err) {
      setError('Failed to encrypt file. Please try again.');
      console.error(err);
    } finally {
      setIsEncrypting(false);
    }
  };

  // Handle sending the encrypted file via email
  const handleSendEmail = async (e) => {
    e.preventDefault();
    
    // Validate inputs
    if (!encryptedFile) {
      setError('Please encrypt a file first');
      return;
    }
    
    // Check if all recipients have email and phone
    const isValidRecipients = recipients.every(r => r.email && r.phone);
    if (!isValidRecipients) {
      setError('Please fill in all recipient email addresses and phone numbers');
      return;
    }
    
    if (!subject) {
      setError('Please enter a subject');
      return;
    }
    
    setIsSending(true);
    setError('');
    
    try {
      // Prepare data for API
      const shareData = {
        recipients: recipients,
        subject: subject,
        message: body,
        fileId: encryptedFile.id, // Assuming the API returns a file ID
        password: password // Include password to be sent via SMS
      };
      
      // API call to send email with encrypted file
      await api.shareViaEmail(shareData);
      
      // Reset form
      setRecipients([{ email: '', phone: '' }]);
      setRecipientCount(1);
      setSubject('');
      setBody('');
      setFile(null);
      setEncryptedFile(null);
      setPassword('');
      
      // Success message
      alert('Email sent successfully! Decryption keys have been sent to recipients via SMS.');
    } catch (err) {
      setError('Failed to send email. Please try again.');
      console.error(err);
    } finally {
      setIsSending(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6 max-w-4xl mx-auto my-8">
      <h2 className="text-2xl font-bold mb-6 text-gray-800">Share Encrypted File</h2>
      
      {error && (
        <div className="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
          {error}
        </div>
      )}
      
      <div>
        {/* From field (auto-filled) */}
        <div className="mb-4">
          <label className="block text-gray-700 font-medium mb-2">From</label>
          <div className="flex items-center border rounded-md px-3 py-2 bg-gray-100">
            <Mail className="text-gray-500 mr-2" size={18} />
            <span>{userEmail}</span>
          </div>
        </div>
        
        {/* Recipients section */}
        <div className="mb-4">
          <div className="flex justify-between items-center mb-2">
            <label className="block text-gray-700 font-medium">To (Recipients)</label>
            <div className="flex items-center">
              <span className="text-sm text-gray-500 mr-2">Recipients: {recipientCount}/4</span>
              {recipientCount < 4 && (
                <button 
                  type="button" 
                  onClick={addRecipient} 
                  className="bg-blue-500 text-white p-1 rounded-full hover:bg-blue-600"
                >
                  <Plus size={16} />
                </button>
              )}
            </div>
          </div>
          
          {recipients.map((recipient, index) => (
            <div key={index} className="mb-3 p-3 border rounded-md bg-gray-50">
              <div className="flex justify-between items-center mb-2">
                <span className="font-medium text-gray-700">Recipient {index + 1}</span>
                {recipients.length > 1 && (
                  <button 
                    type="button" 
                    onClick={() => removeRecipient(index)}
                    className="text-red-500 hover:text-red-700"
                  >
                    <X size={16} />
                  </button>
                )}
              </div>
              
              <div className="flex flex-col md:flex-row gap-3">
                <div className="flex-1">
                  <div className="flex items-center border rounded-md bg-white">
                    <div className="bg-gray-200 p-2 rounded-l-md">
                      <User className="text-gray-500" size={18} />
                    </div>
                    <input
                      type="email"
                      required
                      placeholder="Email address"
                      className="w-full p-2 outline-none"
                      value={recipient.email}
                      onChange={(e) => updateRecipient(index, 'email', e.target.value)}
                    />
                  </div>
                </div>
                
                <div className="flex-1">
                  <div className="flex items-center border rounded-md bg-white">
                    <div className="bg-gray-200 p-2 rounded-l-md">
                      <Phone className="text-gray-500" size={18} />
                    </div>
                    <input
                      type="tel"
                      required
                      placeholder="Phone number for decryption key"
                      className="w-full p-2 outline-none"
                      value={recipient.phone}
                      onChange={(e) => updateRecipient(index, 'phone', e.target.value)}
                    />
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
        
        {/* Subject field */}
        <div className="mb-4">
          <label className="block text-gray-700 font-medium mb-2">Subject</label>
          <input
            type="text"
            required
            className="w-full border rounded-md px-3 py-2"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
          />
        </div>
        
        {/* Message body */}
        <div className="mb-4">
          <label className="block text-gray-700 font-medium mb-2">Message</label>
          <textarea
            className="w-full border rounded-md px-3 py-2 h-32"
            value={body}
            onChange={(e) => setBody(e.target.value)}
          ></textarea>
        </div>
        
        {/* File attachment */}
        <div className="mb-6">
          <label className="block text-gray-700 font-medium mb-2">Attachment (Encrypted)</label>
          <div className="flex items-center">
            <label className="cursor-pointer flex items-center border rounded-md px-4 py-2 bg-gray-100 hover:bg-gray-200">
              <Paperclip className="mr-2" size={18} />
              <span>{file ? file.name : 'Choose file'}</span>
              <input 
                type="file" 
                className="hidden" 
                onChange={handleFileChange}
              />
            </label>
            {file && !encryptedFile && (
              <button 
                type="button" 
                className="ml-2 text-red-500 hover:text-red-700"
                onClick={() => setFile(null)}
              >
                <X size={18} />
              </button>
            )}
          </div>
          
          {file && !encryptedFile && (
            <div className="mt-2 text-sm text-yellow-600 flex items-center">
              <span className="mr-1">●</span> File needs to be encrypted before sending
            </div>
          )}
          
          {encryptedFile && (
            <div className="mt-2 text-sm text-green-600 flex items-center">
              <span className="mr-1">●</span> File encrypted and ready to send
            </div>
          )}
        </div>
        
        {/* Action buttons */}
        <div className="flex justify-end space-x-4">
          <button
            type="button"
            onClick={handleEncryptDialog}
            disabled={!file || isEncrypting || encryptedFile}
            className={`flex items-center px-6 py-2 rounded-md text-white font-medium ${
              !file || isEncrypting || encryptedFile ? 'bg-green-400' : 'bg-green-600 hover:bg-green-700'
            }`}
          >
            <Lock size={18} className="mr-2" />
            {isEncrypting ? 'Encrypting...' : 'Encrypt File'}
          </button>
          
          <button
            type="button"
            onClick={handleSendEmail}
            disabled={!encryptedFile || isSending}
            className={`flex items-center px-6 py-2 rounded-md text-white font-medium ${
              !encryptedFile || isSending ? 'bg-blue-400' : 'bg-blue-600 hover:bg-blue-700'
            }`}
          >
            <Send size={18} className="mr-2" />
            {isSending ? 'Sending...' : 'Send'}
          </button>
        </div>
      </div>
      
      {/* Encryption password dialog */}
      {isDialogOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h3 className="text-xl font-bold mb-4">Encrypt File</h3>
            <p className="mb-4 text-gray-600">Enter a password to encrypt your file. This password will be sent to recipient's phone number.</p>
            
            <div className="mb-4">
              <label className="block text-gray-700 font-medium mb-2">Password</label>
              <input
                type="password"
                className="w-full border rounded-md px-3 py-2"
                placeholder="Enter encryption password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
            
            <div className="flex justify-end space-x-3">
              <button
                type="button"
                className="px-4 py-2 border rounded-md"
                onClick={() => {
                  setIsDialogOpen(false);
                }}
              >
                Cancel
              </button>
              <button
                type="button"
                className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
                onClick={handleEncryptFile}
                disabled={!password || isEncrypting}
              >
                {isEncrypting ? 'Encrypting...' : 'Encrypt File'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}