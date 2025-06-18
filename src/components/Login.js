import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../services/auth';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isRegistering, setIsRegistering] = useState(false);
  const [name, setName] = useState('');
  const [error, setError] = useState('');
  
  const { login, register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    try {
      if (isRegistering) {
        if (!name) {
          setError('Name is required');
          return;
        }
        await register({ name, email, password });
      } else {
        await login(email, password);
      }
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || `Failed to ${isRegistering ? 'register' : 'login'}`);
    }
  };

  const handleOAuthLogin = (provider) => {
    window.location.href = authService.getOAuthLoginUrl(provider);
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card shadow">
            <div className="card-body p-4">
              <h2 className="text-center mb-4">
                {isRegistering ? 'Create Account' : 'Sign In'}
              </h2>
              
              {error && <div className="alert alert-danger">{error}</div>}
              
              <form onSubmit={handleSubmit}>
                {isRegistering && (
                  <div className="mb-3">
                    <label htmlFor="name" className="form-label">Full Name</label>
                    <input
                      type="text"
                      className="form-control"
                      id="name"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      required
                    />
                  </div>
                )}
                
                <div className="mb-3">
                  <label htmlFor="email" className="form-label">Email</label>
                  <input
                    type="email"
                    className="form-control"
                    id="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
                
                <div className="mb-3">
                  <label htmlFor="password" className="form-label">Password</label>
                  <input
                    type="password"
                    className="form-control"
                    id="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
                
                <button type="submit" className="btn btn-primary w-100 mb-3">
                  {isRegistering ? 'Register' : 'Login'}
                </button>
              </form>
              
              <div className="text-center mb-3">
                <button className="btn btn-link" onClick={() => setIsRegistering(!isRegistering)}>
                  {isRegistering ? 'Already have an account? Sign in' : "Don't have an account? Sign up"}
                </button>
              </div>
              
              <hr className="my-4" />
              
              <div className="text-center">
                <p>Or continue with</p>
                <div className="d-flex justify-content-center gap-2">
                  <button 
                    className="btn btn-outline-dark" 
                    onClick={() => handleOAuthLogin('google')}
                  >
                    <i className="fab fa-google me-2"></i>Google
                  </button>
                  <button 
                    className="btn btn-outline-primary" 
                    onClick={() => handleOAuthLogin('github')}
                  >
                    <i className="fab fa-github me-2"></i>GitHub
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;