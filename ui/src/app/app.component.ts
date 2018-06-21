import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title = 'app';
  data : HModel[] = [{
    class: 1,
    text:'Activations that are more complex than a simple TensorFlow/Theano/CNTK function (eg. learnable activations)',
    tags:['Keras','Machine Learning'],
    url:'http://www.google.com',
    source: 'Twitter'
  },{
    class: 0,
    text:'A bad Review',
    tags:['Demo','Test'],
    url:'http://www.google.com',
    source: 'Reddit'
  }]
}

export interface HModel{
  class:number
  url?:string
  source?:string
  tags?: string[]
  text:string
}
