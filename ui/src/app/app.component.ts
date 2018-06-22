import { Component } from '@angular/core';
import { BackService } from './back.service'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  title = 'app';
  data : HModel[] = []
  // [{
  //   class: 1,
  //   text:'Activations that are more complex than a simple TensorFlow/Theano/CNTK function (eg. learnable activations)',
  //   tags:['Keras','Machine Learning'],
  //   url:'http://www.google.com',
  //   source: 'Twitter'
  // },{
  //   class: 0,
  //   text:'A bad Review',
  //   tags:['Demo','Test'],
  //   url:'http://www.google.com',
  //   source: 'Reddit'
  // }]

  constructor(private backService: BackService) { 
    console.log(backService.get(null))
  }
  ngOnInit() {
    this.fetch()
    setInterval(()=> this.fetch(),5000)
  }

  private fetch():void{
    try{
      this.backService.get(null).subscribe(data =>{
        const cdata = data['_embedded']['comments']
        this.data = cdata.map(c =>{
          let r:number = 0
          if (c['rating'] > .8){
            r = 1
          }else if(c['rating'] < .2){
            r = -1
          }
          var d = new Date(0); // The 0 there is the key, which sets the date to the epoch
          d.setUTCSeconds(c['timeStamp']);
          return {
            class: r,
            text: c['text'],
            tags: c['tags'],
            timeStamp: d,
            source: c['source'],
            url: c['url'],
            rating: c['rating']
          }
        })
      })
    }catch(e){

    }
  }
}

export interface HModel{
  class:number
  url?:string
  source?:string
  tags?: string[]
  text:string
  timeStamp?:Date
  rating?:number
}
