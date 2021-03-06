import React from 'react';
import {useState} from 'react';
import './App.css';
import {Title, SelectedTitle, FormWrapper, Button, Arrow} from './Modals';

export default function(props) {
  const [pass, setPass] = useState("");
  return (
    <form id="signup-form" onSubmit={props.handleSubmit}>
      <div className="selectWrapper">
        <Title onClick={()=>{props.setSelectLogin(true); props.setAlertText("")}}>Login</Title>
        <SelectedTitle onClick={()=>{props.setSelectLogin(false)}}>Signup</SelectedTitle>
      </div>
      
      <FormWrapper>
        <div className="fields">
          <input type="text" placeholder="Username" onChange={(e) => {
            props.setUsername(e.target.value);
          }}
            style={{borderColor: props.validUserName ? 'green':'red'}}  
          />
          <input type="password" placeholder="Password" onChange={(e) => {
            props.setPassword(e.target.value);
          }}
            style={{borderColor: props.validPass ? 'green':'red'}} 
          />
          <input type="password" placeholder="Retype Password"
            onChange={(e) => {
              setPass(e.target.value);
              props.setConfirmPassword(e.target.value);
            }}
            style={{borderColor: pass === props.password && pass.length !== 0? 'green':'red'}} 
          />
        </div>
        <Button>
          create user
          <Arrow className="arrow"></Arrow>
        </Button>
      </FormWrapper>
      
      <div className="alertWrapper">
      	<p>{props.alertText}</p>
      </div>
      
    </form>
  );
}
